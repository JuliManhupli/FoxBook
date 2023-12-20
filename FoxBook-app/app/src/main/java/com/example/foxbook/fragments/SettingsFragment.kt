package com.example.foxbook.fragments

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.foxbook.R
import com.example.foxbook.SharedViewModel
import kotlin.properties.Delegates


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var exampleText: TextView

    private var currentBg by Delegates.notNull<Int>()
    private var currentSampleTextColor by Delegates.notNull<Int>()
    private var currentTextSize by Delegates.notNull<Float>()
    private lateinit var currentFont: Typeface

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // до профілю
        val backToProfile: ImageButton = view.findViewById(R.id.backToProfileFromSettings)
        backToProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, profileFragment)
            transaction.addToBackStack(null)
            transaction.commit()
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


        currentBg = resources.getColor(beigeColor)
        currentSampleTextColor = resources.getColor(blackColor)
        currentTextSize = exampleText.textSize
        currentFont = exampleText.typeface


        whiteBG.setOnClickListener {
            setBGColor(whiteColor, blackColor)
        }

        beigeBG.setOnClickListener {
            setBGColor(beigeColor, blackColor)
        }

        greyBG.setOnClickListener {
            setBGColor(greyColor, whiteColor)
        }

        blackBG.setOnClickListener {
            setBGColor(blackColor, whiteColor)
        }

        // збільшення/зменшення тексту
        val sizeSmallerBtn: Button = view.findViewById(R.id.btnChangeTextSizeSmall)
        val sizeBiggerBtn: Button = view.findViewById(R.id.btnChangeTextSizeBig)

        sizeSmallerBtn.setOnClickListener {
            exampleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, exampleText.textSize - 1)
            Log.d("TEXTSIZE", exampleText.textSize.toString())
            currentTextSize = exampleText.textSize
        }

        sizeBiggerBtn.setOnClickListener {
            exampleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, exampleText.textSize + 1)
            Log.d("TEXTSIZE", exampleText.textSize.toString())
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
            "Sans-serif-smallcaps")

        // Адаптер для шрифтів
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOfSortings)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        // При виборі елементу
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                        Log.d("TXTFONT", "Here")
                        val customFont = ResourcesCompat.getFont(context!!, R.font.inter)
                        exampleText.typeface = customFont
                } else {
                    val chosenFont = parent.getItemAtPosition(position).toString().lowercase()
                    exampleText.typeface = Typeface.create(chosenFont, Typeface.NORMAL)
                }
                currentFont = exampleText.typeface
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        // встановлення всіх параметрів для тексту
        val changeSettingsBtn: Button = view.findViewById(R.id.btnChangeTextBookSettings)

        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        changeSettingsBtn.setOnClickListener {
            Log.d("TEXTDATA", currentBg.toString())
            Log.d("TEXTDATA", currentSampleTextColor.toString())
            Log.d("TEXTDATA", currentTextSize.toString())
            Log.d("TEXTDATA", currentFont.toString())
            val tempArray = arrayListOf(
                currentBg, currentSampleTextColor, currentTextSize, currentFont
            )
            sharedViewModel.data.value = tempArray
            Log.d("AAAAAAAAAAAAAAAAAAAAA", sharedViewModel.data.value.toString())
            Toast.makeText(context, "Дані збережено", Toast.LENGTH_LONG).show()
        }
    }

    private fun setBGColor(bgColor: Int, textColor: Int) {
        exampleText.setBackgroundColor(resources.getColor(bgColor))
        exampleText.setTextColor(resources.getColor(textColor))

        currentBg = resources.getColor(bgColor)
        currentSampleTextColor = resources.getColor(textColor)
    }

}