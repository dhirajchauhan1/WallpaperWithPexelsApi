package com.hdwallpaper4K.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hdwallpaper4K.BuildConfig
import com.hdwallpaper4K.databinding.FragmentProfileBinding
import com.hdwallpaper4K.ui.AboutActivity
import es.dmoral.toasty.Toasty


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        binding.tvVersion.text = "$versionName"

        binding.clearCache.setOnClickListener {
            activity?.cacheDir?.deleteRecursively()
            Toasty.success(this.requireContext(), "Cache Clear", Toasty.LENGTH_SHORT).show()
        }

        binding.tellFriend.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                    
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        }

        binding.feedback.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "chauhandhiraj757@gmail.com", null
                )
            )
            intent.putExtra(Intent.EXTRA_SUBJECT, "Wallpaper Application Feedback")
            startActivity(Intent.createChooser(intent, "Choose an Email client :"))
        }

        binding.rate.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${context?.packageName}")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${context?.packageName}")
                    )
                )
            }
        }

        binding.about.setOnClickListener {
            startActivity(Intent(context, AboutActivity::class.java))
        }

        return binding.root
    }


}