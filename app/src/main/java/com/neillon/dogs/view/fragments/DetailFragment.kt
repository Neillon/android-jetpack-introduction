package com.neillon.dogs.view.fragments


import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.neillon.dogs.R
import com.neillon.dogs.databinding.FragmentDetailBinding
import com.neillon.dogs.databinding.SendSmsDialogBinding
import com.neillon.dogs.model.Dog
import com.neillon.dogs.model.DogPalette
import com.neillon.dogs.model.SmsInfo
import com.neillon.dogs.view.activities.MainActivity
import com.neillon.dogs.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.send_sms_dialog.*

class DetailFragment : Fragment() {

    private var dogUUid = -1
    private lateinit var viewModel: DetailViewModel

    private lateinit var databinding: FragmentDetailBinding
    private var sendSmsStarted = false

    private var currentDog: Dog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        databinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUUid = DetailFragmentArgs.fromBundle(it).dogUUId
        }
        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUUid)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogLiveData.observe(this, Observer { dog ->
            currentDog = dog
            dog?.let {
                databinding.dog = dog
                it.imageUrl?.let {
                    setupBackgroundColor(it)
                }

            }
        })
    }

    private fun setupBackgroundColor(uri: String) {
        Glide.with(this)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->
                        val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                        val myPalette = DogPalette(intColor)
                        databinding.dogPalette = myPalette
                    }
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                sendSmsStarted = true
                (activity as MainActivity).checkSMSPermission()
            }

            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed")
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "${currentDog?.dogBread} bred for ${currentDog?.breadFor}"
                )
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent, "Share it"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context?.let {
                var smsInfo =
                    SmsInfo(
                        "",
                        "${currentDog?.dogBread} bred for ${currentDog?.breadFor}",
                        currentDog?.imageUrl
                    )

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )

                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS") { dialog, wich ->
                        if (!dialogBinding.smsDestinantion.text.isNullOrEmpty()) {
                            smsInfo.to = dialogBinding.smsDestinantion.text.toString()
                            sendSMS(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which -> }
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSMS(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }
}
