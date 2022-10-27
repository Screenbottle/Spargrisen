package com.example.spargrisen

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.example.spargrisen.Language
import com.example.spargrisen.ResultOrError
import com.example.spargrisen.SmoothedMutableLiveData
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions
import com.example.spargrisen.CameraFragment.Companion.DESIRED_HEIGHT_CROP_PERCENT
import com.example.spargrisen.CameraFragment.Companion.DESIRED_WIDTH_CROP_PERCENT
import com.google.mlkit.nl.translate.Translation.*
import com.google.mlkit.nl.translate.Translator

class CameraViewModel (application: Application) : AndroidViewModel(application) {

    // TODO Instantiate LanguageIdentification
    val targetLang = MutableLiveData<Language>()
    val sourceText = SmoothedMutableLiveData<String>(SMOOTHING_DURATION)
    private val languageIdentification = LanguageIdentification.getClient()

    // We set desired crop percentages to avoid having to analyze the whole image from the live
    // camera feed. However, we are not guaranteed what aspect ratio we will get from the camera, so
    // we use the first frame we get back from the camera to update these crop percentages based on
    // the actual aspect ratio of images.
    val imageCropPercentages = MutableLiveData<Pair<Int, Int>>()
        .apply { value = Pair(DESIRED_HEIGHT_CROP_PERCENT, DESIRED_WIDTH_CROP_PERCENT) }
    val translatedText = MediatorLiveData<ResultOrError>()
    private val translating = MutableLiveData<Boolean>()
    val modelDownloading = SmoothedMutableLiveData<Boolean>(SMOOTHING_DURATION)

    private var modelDownloadTask: Task<Void> = Tasks.forCanceled()

    private val translators =
        object : LruCache<TranslatorOptions, Translator>(NUM_TRANSLATORS) {
            override fun create(options: TranslatorOptions): Translator {
                return getClient(options)
            }

            override fun entryRemoved(
                evicted: Boolean,
                key: TranslatorOptions,
                oldValue: Translator,
                newValue: Translator?
            ) {
                oldValue.close()
            }
        }

    val sourceLang = Transformations.switchMap(sourceText) { text ->
        val result = MutableLiveData<Language>()
        languageIdentification.identifyLanguage(text)
            .addOnSuccessListener {
                if (it != "und")
                    result.value = Language(it)
            }
        result
    }

    override fun onCleared() {
        // TODO Shut down ML Kit clients.
    }

    private fun translate(): Task<String> {
        val text = sourceText.value
        val source = sourceLang.value
        val target = targetLang.value
        if (modelDownloading.value != false || translating.value != false) {
            return Tasks.forCanceled()
        }
        if (source == null || target == null || text == null || text.isEmpty()) {
            return Tasks.forResult("")
        }
        val sourceLangCode = TranslateLanguage.fromLanguageTag(source.code)
        val targetLangCode = TranslateLanguage.fromLanguageTag(target.code)
        if (sourceLangCode == null || targetLangCode == null) {
            return Tasks.forCanceled()
        }
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode)
            .setTargetLanguage(targetLangCode)
            .build()
        val translator = translators[options]
        modelDownloading.setValue(true)

        // Register watchdog to unblock long running downloads
        Handler().postDelayed({ modelDownloading.setValue(false) }, 15000)
        modelDownloadTask = translator.downloadModelIfNeeded().addOnCompleteListener {
            modelDownloading.setValue(false)
        }
        translating.value = true
        return modelDownloadTask.onSuccessTask {
            translator.translate(text)
        }.addOnCompleteListener {
            translating.value = false
        }
    }

    // Gets a list of all available translation languages.
    val availableLanguages: List<Language> = TranslateLanguage.getAllLanguages()
        .map { Language(it) }

    init {
        modelDownloading.setValue(false)
        translating.value = false
        // Create a translation result or error object.
        val processTranslation =
            OnCompleteListener { task ->
                if (task.isSuccessful) {
                    translatedText.value = ResultOrError(task.result, null)
                } else {
                    if (task.isCanceled) {
                        // Tasks are cancelled for reasons such as gating; ignore.
                        return@OnCompleteListener
                    }
                    translatedText.value = ResultOrError(null, task.exception)
                }
            }
        // Start translation if any of the following change: detected text, source lang, target lang.
        translatedText.addSource(sourceText) { translate().addOnCompleteListener(processTranslation) }
        translatedText.addSource(sourceLang) { translate().addOnCompleteListener(processTranslation) }
        translatedText.addSource(targetLang) { translate().addOnCompleteListener(processTranslation) }
    }

    companion object {
        // Amount of time (in milliseconds) to wait for detected text to settle
        private const val SMOOTHING_DURATION = 50L

        private const val NUM_TRANSLATORS = 1
    }
}