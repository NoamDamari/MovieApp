package com.example.movieapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.movieapp.databinding.ActivityMainBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val order = Order(null , null , 0 , 0)

        // View Binding initialization
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set initial price
        binding.priceTV.text = getString(R.string.initialPrice , 0)

        ///////////////////////////// Images Switcher ///////////////////////////////////

        // Images list
        val imagesList = listOf(
            R.drawable.avengers1,
            R.drawable.avengers2,
            R.drawable.avengers3
        )
        var position = 0

        // Images Switcher
        binding.imageSwitcher.setFactory {

            val imageView = ShapeableImageView(this@MainActivity)

            // Setting image design attributes
            imageView.apply {
                setImageResource(imagesList[0])
                // Image scale
                scaleType = ImageView.ScaleType.FIT_XY
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                )
                setPadding(8, 8, 8, 8)

                // Rounded corners
                shapeAppearanceModel = this.shapeAppearanceModel
                    .toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, 40f)
                    .build()
            }
            imageView
        }

        val imageSwitcherManager = ImageSwitcherManager(binding.imageSwitcher)

        // Switch to the right when button is clicked
        binding.rightBtn.setOnClickListener {
            // Position update
            if (position < imagesList.size - 1) position++ else position = 0
            imageSwitcherManager.rightSwitch(imagesList, position)
        }
        // Switch to the left when button is clicked
        binding.leftBtn.setOnClickListener {
            // Position update
            if (position > 0) position-- else position = imagesList.size - 1
            imageSwitcherManager.leftSwitch(imagesList, position)
        }

        ///////////////////////////// Movie Description ///////////////////////////////////

        val animator = Animator()
        // Show whether the movie description open or not
        var opened = false

        // Click handler to expand or collapse the movie description view
        binding.descriptionBtn.setOnClickListener {
            opened = if(!opened) {

                animator.expandViewAnim(binding.descriptionLayout)

                binding.descriptionBtn.apply {
                    text = getString(R.string.hide_description)
                    setIconResource(R.drawable.icon_collapse)
                }
                true
            }else {

                animator.collapseViewAnim(binding.descriptionLayout)

                binding.descriptionBtn.apply {
                    text = getString(R.string.show_description)
                    setIconResource(R.drawable.icon_expand)
                }
                false
            }
        }

        ///////////////////////////// Date Picker Dialog ///////////////////////////////////

        // Create date picker dialog
        val datePicker: MaterialDatePicker<Long> by lazy {
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())

            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setTheme(R.style.DatePickerTheme_App)
                .build()
        }

        // Show date picker dialog when button clicked
        binding.dateBtn.setOnClickListener {
            datePicker.show(supportFragmentManager, "date_picker")
        }

        // Click handler to set selected date
        datePicker.addOnPositiveButtonClickListener {
            binding.dateBtn.text = datePicker.headerText
        }

        ///////////////////////////// Theater Picker Dialog ///////////////////////////////////

        // Create theater picker dialog
        val theaterPickerDialog : AlertDialog by lazy {
            val theaterDialogLayout = layoutInflater.inflate(R.layout.theater_picker_dialog , null)
            val dialogBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
            val dialog = dialogBuilder.setView(theaterDialogLayout).create()

            val radioGroup = theaterDialogLayout.findViewById<RadioGroup>(R.id.radioGroup)
            val okBtn = theaterDialogLayout.findViewById<Button>(R.id.theaterDialogOKBtn)
            val cancelBtn = theaterDialogLayout.findViewById<Button>(R.id.theaterDialogCancelBtn)

            // Click handler to set selected theater
            okBtn.setOnClickListener {
                // Retrieve the selected RadioButton within the RadioGroup
                val selectedTheater = radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                binding.theaterBtn.text = selectedTheater.text
                dialog.dismiss()
            }

            // Close dialog when cancel button is clicked
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog
        }

        // Show theater picker dialog when button is clicked
        binding.theaterBtn.setOnClickListener {
            theaterPickerDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()
            }
        }

        /////////////////////////////  Tickets Number ///////////////////////////////////

        // Display and update the total price whenever the number of tickets changes
        binding.adultTicketsTF.addTextChangedListener {
            binding.priceTV.text = updatePrice()
        }
        binding.childTicketsTF.addTextChangedListener {
            binding.priceTV.text = updatePrice()
        }

        ///////////////////////////// Order Confirmation Dialog ///////////////////////////////////

        // Create order confirmation dialog
        val orderConfirmationDialog : AlertDialog by lazy {
            val dialogLayout = layoutInflater.inflate(R.layout.confirmation_dialog , null)
            val dialogBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
            val dialog = dialogBuilder.setView(dialogLayout).create()

            val confirmedDateTV = dialogLayout.findViewById<TextView>(R.id.confirmedDate)
            val confirmedTheaterTV = dialogLayout.findViewById<TextView>(R.id.confirmedTheater)
            val confirmedTicketsTV = dialogLayout.findViewById<TextView>(R.id.confirmedTickets)
            val confirmBtn = dialogLayout.findViewById<Button>(R.id.finishButton)

            // Close dialog click listener
            confirmBtn?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.apply {

                // Setting order confirmation details on dialog show
                setOnShowListener {
                    confirmedDateTV.text = order.date
                    confirmedTheaterTV.text = order.theater
                    confirmedTicketsTV.text = when {
                        order.childTickets <= 0 && order.adultTickets > 0 -> "${order.adultTickets} " + getString(
                            R.string.adult_tickets
                        )
                        order.childTickets > 0 && order.adultTickets <= 0 -> "${order.childTickets} " + getString(
                            R.string.child_tickets
                        )
                        else -> "${order.adultTickets} " + getString(R.string.adult_tickets) + "\n" + "${order.childTickets} " + getString(
                            R.string.child_tickets
                        )
                    }
                }

                // Listener for re-initialize the activity when the dialog is dismissed
                setOnDismissListener {
                    val intent = intent
                    finish()
                    startActivity(intent)
                }

                // Setting the background drawable of the dialog window to transparent
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog
        }

        ///////////////////////////// Order Details Dialog ///////////////////////////////////

        // Create order details dialog
        val orderDetailsDialog : AlertDialog by lazy {

            val orderDialogLayout = layoutInflater.inflate(R.layout.order_details_dialog , null)
            val orderDialogBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
            val dialog = orderDialogBuilder.setView(orderDialogLayout).create()

            val dateTV = orderDialogLayout.findViewById<TextView>(R.id.dateTV)
            val theaterTV = orderDialogLayout.findViewById<TextView>(R.id.theaterTV)
            val childTicketsTV = orderDialogLayout.findViewById<TextView>(R.id.childTicketsTV)
            val adultTicketsTV = orderDialogLayout.findViewById<TextView>(R.id.adultTicketsTV)
            val totalPriceTV = orderDialogLayout.findViewById<TextView>(R.id.totalPriceTV)
            val confirmBtn = orderDialogLayout.findViewById<Button>(R.id.confirmBtn)
            val orderCancelBtn = orderDialogLayout.findViewById<Button>(R.id.cancelBtn)

            // Setting order details on dialog show
            dialog.setOnShowListener {
                dateTV.text = order.date
                theaterTV.text = order.theater
                adultTicketsTV.text = order.adultTickets.toString()
                childTicketsTV.text = order.childTickets.toString()
                totalPriceTV.text = binding.priceTV.text.toString()
            }

            // Dismiss order details dialog and show confirmation dialog
            confirmBtn.setOnClickListener {
                dialog.dismiss()
                orderConfirmationDialog.show()
            }

            // Close dialog click listener
            orderCancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog
        }

        ///////////////////////////// Get Tickets Button ///////////////////////////////////
        binding.getTicketsBtn.setOnClickListener {

            // Setting order details
            order.apply {
                this.date = binding.dateBtn.text.toString()
                this.theater = binding.theaterBtn.text.toString()
                this.adultTickets = binding.adultTicketsTF.text.toString().toInt()
                this.childTickets = binding.childTicketsTF.text.toString().toInt()
            }

            // Order values validation
            if(order.validateOrder()) {
                orderDetailsDialog.apply {
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }
            }
            else {
                Toast.makeText(this,getString(R.string.validation_alert), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Update price function
    private fun updatePrice(): String {
        val adultTickets = binding.adultTicketsTF.text.toString().toInt()
        val childTickets = binding.childTicketsTF.text.toString().toInt()

        return (adultTickets * 16 + childTickets * 12).toString() + getString(R.string.payment_sign)
    }
}

