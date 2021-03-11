package com.hdwallpaper4K.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import com.hdwallpaper4K.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val txt = """Hi, my name is Dhiraj Chauhan and I am a freelance Android App Developer.

if you have any suggestions or feedback feel free to message me. I will reply to everyone. :)

Do drop me a Hi.. , it really means a lot to me.Looking forward to hear from you.
 
Thank you..."""
        val spannableString = SpannableString(txt)
        val boldItalicSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldItalicSpan, 15, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.developerDescription.text = spannableString


        binding.instagram.setOnClickListener {
            openInsta()
        }
        binding.linkedin.setOnClickListener {
            openLinkedin()
        }

        binding.back.setOnClickListener {
            finish()
        }
    }

    fun openInsta(){
        val uri: Uri = Uri.parse("https://www.instagram.com/dhiraj.apk")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)

        likeIng.setPackage("com.instagram.android")

        try {
            startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(uri.toString())
                )
            )
        }
    }

    fun openLinkedin(){
        val uri: Uri = Uri.parse("https://www.linkedin.com/in/dhiraj-chauhan-717bb0179")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)

        likeIng.setPackage("com.linkedin.android")

        try {
            startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(uri.toString())
                )
            )
        }
    }
}