package com.example.movieapp

import android.widget.ImageSwitcher

class ImageSwitcherManager(private val imageSwitcher: ImageSwitcher) {

    // Right Switch with animations
   fun rightSwitch(imagesList : List<Int> , position : Int) {

       imageSwitcher.apply {
           // Set animations to the right
           setInAnimation(context , R.anim.slide_in_right)
           setOutAnimation(context , R.anim.slide_out_right)
           // Switch
           setImageResource(imagesList[position])
       }
   }

    fun leftSwitch(imagesList : List<Int> , position : Int) {

        imageSwitcher.apply {
            // Set animations to the left
            setInAnimation(context , R.anim.slide_in_left)
            setOutAnimation(context, R.anim.slide_out_left)
            // Switch
            setImageResource(imagesList[position])
        }
    }
}