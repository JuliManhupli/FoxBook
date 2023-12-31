package com.example.foxbook.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle

import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.foxbook.ClientAPI.apiService
import com.example.foxbook.R
import com.example.foxbook.api.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var exampleText: TextView

    private lateinit var currentBg: String
    private lateinit var currentSampleTextColor: String
    private var currentTextSize by Delegates.notNull<Float>()
    private lateinit var currentFont: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // до профілю
        val backToProfile: ImageButton = view.findViewById(R.id.backToProfileFromSettings)
        backToProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, profileFragment)
            transaction.addToBackStack("$profileFragment")
            transaction.commit()
        }

        // Перевірка на дозвіл зміни яскравості
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                startActivity(intent)
            }
        }

        val contentResolver = requireActivity().contentResolver
        val seekBrightnessBar: SeekBar = view.findViewById(R.id.seekBarBrightness)
        val brightness: Int =
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0)
        seekBrightnessBar.progress = brightness

        val canWrite = Settings.System.canWrite(context)
        if (canWrite) {
            if (Settings.System.getInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    0
                ) != Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            ) {
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                )
            }

            // Зміна яскравості
            seekBrightnessBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val validBrightness = kotlin.math.max(1, kotlin.math.min(progress, 255))
                    Settings.System.putInt(
                        contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        validBrightness
                    )
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            startActivity(intent)
        }

        // текстове поле
        exampleText = view.findViewById(R.id.sampleBookText)

        // фони для тексту
        val whiteBG: CardView = view.findViewById(R.id.cardReadingBGWhite)
        val whiteColor = R.color.white

        val beigeBG: CardView = view.findViewById(R.id.cardReadingBGBeige)
        val beigeColor = R.color.book_bg

        val greyBG: CardView = view.findViewById(R.id.cardReadingBGGrey)
        val greyColor = R.color.settings_grey_bg

        val blackBG: CardView = view.findViewById(R.id.cardReadingBGBlack)
        val blackColor = R.color.black


        currentBg = "beige"
        currentSampleTextColor = "black"
        currentTextSize = exampleText.textSize
        currentFont = "inter"


        whiteBG.setOnClickListener {
            setBGColor(whiteColor, blackColor, "white", "black")

        }

        beigeBG.setOnClickListener {
            setBGColor(beigeColor, blackColor, "beige", "black")
        }

        greyBG.setOnClickListener {
            setBGColor(greyColor, whiteColor, "grey", "white")
        }

        blackBG.setOnClickListener {
            setBGColor(blackColor, whiteColor, "black", "white")
        }

        // збільшення/зменшення тексту
        val sizeSmallerBtn: Button = view.findViewById(R.id.btnChangeTextSizeSmall)
        val sizeBiggerBtn: Button = view.findViewById(R.id.btnChangeTextSizeBig)

        sizeSmallerBtn.setOnClickListener {
            exampleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, exampleText.textSize - 1)
            currentTextSize = exampleText.textSize
        }

        sizeBiggerBtn.setOnClickListener {
            exampleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, exampleText.textSize + 1)
            currentTextSize = exampleText.textSize
        }

        // Зміфна шрифтів
        val spinner: Spinner = view.findViewById(R.id.spnrFontsChange)

        val listOfSortings = listOf(
            "Inter",
            "Sans-serif-light",
            "Sans-serif-thin",
            "Sans-serif-black",
            "Sans-serif-condensed",
            "Sans-serif-condensed-light",
            "Sans-serif-smallcaps"
        )

        // Адаптер для шрифтів
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOfSortings)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        // При виборі елементу
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    val customFont = ResourcesCompat.getFont(requireContext(), R.font.inter)
                    exampleText.typeface = customFont
                    currentFont = "inter"
                } else {
                    val chosenFont = parent.getItemAtPosition(position).toString().lowercase()
                    exampleText.typeface = Typeface.create(chosenFont, Typeface.NORMAL)
                    currentFont = chosenFont
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }


        // встановлення всіх параметрів для тексту
        val changeSettingsBtn: Button = view.findViewById(R.id.btnChangeTextBookSettings)
        changeSettingsBtn.setOnClickListener {
            sendReadingSettingsToAPI()
        }
    }

    private fun setBGColor(bgColor: Int, textColor: Int, bgColorStr: String, textColorStr: String) {
        exampleText.setBackgroundColor(resources.getColor(bgColor))
        exampleText.setTextColor(resources.getColor(textColor))
        currentBg = bgColorStr
        currentSampleTextColor = textColorStr
    }

    private fun sendReadingSettingsToAPI() {
        val readingSettingsRequest = UserData.ReadingSettings(
            bg_color = currentBg,
            text_color = currentSampleTextColor,
            text_size = currentTextSize,
            text_font = currentFont.toString()
        )

        val call = apiService.addReadingSettings(readingSettingsRequest)

        call.enqueue(object : Callback<UserData.Message> {
            override fun onResponse(call: Call<UserData.Message>, response: Response<UserData.Message>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Налаштування встановлено!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        requireContext(), "Помилка встановлення налаштувань!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserData.Message>, t: Throwable) {
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()

            }
        })
    }
}