package com.example.spargrisen


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spargrisen.databinding.ActivityNavbarBinding
import com.example.spargrisen.fragments.CameraFragment
import com.example.spargrisen.fragments.GraphFragment
import com.example.spargrisen.fragments.HomeFragment
import com.example.spargrisen.fragments.SettingsFragment
import com.example.spargrisen.utils.PDFListener
import com.gkemon.XMLtoPDF.PdfGenerator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.view.*

var userID: Int = 0// Users ID

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




            setContentView(R.layout.activity_navbar)
            val viewbinding = ActivityNavbarBinding.inflate(layoutInflater)

            setContentView(viewbinding.root)
            replaceFragment(GraphFragment())
            viewbinding.bottomNavigation.setOnItemSelectedListener {

                when (it.itemId) {
                    R.id.ic_graph -> replaceFragment(GraphFragment())
                    R.id.ic_home -> replaceFragment(HomeFragment())
                    R.id.ic_settings -> replaceFragment(SettingsFragment())



                }
                true
            }


    }

    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
            currentFragment = fragment
        }
    }

    private fun doPrint() {

        if (currentFragment.view != null) {
            val pdfListener = PDFListener()


            PdfGenerator.getBuilder()
                .setContext(this)
                .fromViewSource()
                .fromView(currentFragment.requireView().relative_layout)
                .setFileName("Test-PDF")
                .setFolderNameOrPath("Test-PDF-Folder")
                .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.NONE)
                .build(pdfListener)


            // Get a PrintManager instance
            val printManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager
            // Set job name, which will be displayed in the print queue
            val jobName = "${this.getString(R.string.app_name)} Document"
            // Start a print job, passing in a PrintDocumentAdapter implementation
            // to handle the generation of a print document
            printManager.print(jobName, MyPrintDocumentAdapter(this), null)


        }
    }
}

