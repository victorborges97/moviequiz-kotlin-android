package br.borges.moviequiz.ui.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.view.Gravity
import android.view.View
import br.borges.moviequiz.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.lang.String
import java.util.*


class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aboutPage: View = AboutPage(this)
            .isRTL(false)
            .setImage(R.drawable.logo)
            .setDescription(getString(R.string.description_app))
            .addItem(Element().setTitle("Version 1.0.0"))
            .addGroup("Fala conosco")
            .addEmail("borges.jvdo@gmail.com")
            .addWebsite("https://joaovictorborges.dev.br/")
            .addPlayStore("br.com.jvborges.moviequiz")
            .addGitHub("victorborges97")
            .addItem(getCopyRightsElement())
            .create()

        setContentView(aboutPage)

        supportActionBar?.title = "Sobre"

        //setContentView(R.layout.activity_about)
    }

    fun getCopyRightsElement(): Element {
        val copyRightsElement = Element()
        val copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR))
        copyRightsElement.title = copyrights
        copyRightsElement.iconDrawable = R.drawable.about_icon_copy_right
        copyRightsElement.autoApplyIconTint = true
        copyRightsElement.iconTint = R.color.appPrimary
        copyRightsElement.iconNightTint = android.R.color.white
        copyRightsElement.gravity = Gravity.CENTER
        copyRightsElement.onClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(applicationContext, copyrights, Toast.LENGTH_SHORT).show()
            }
        }
        return copyRightsElement
    }
}