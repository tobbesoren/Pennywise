package com.example.pennywise

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pennywise.CameraScanner.Companion.MEDIA_REQUEST_CODE
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class CameraScanner : AppCompatActivity() {

    private lateinit var input : EditText
    private lateinit var inputImageBtn: ImageButton
    private lateinit var recognizeTextBtn: ImageButton
    private lateinit var imageIv: ImageView



    private companion object{
        private const val CAMERA_REQUEST_CODE =100
        private const val STORAGE_REQUEST_CODE =101
        private const val MEDIA_REQUEST_CODE =102
    }

    // Uri of the image from  camera/gallery
    private var imageUri: Uri? = null

    // arrays of the permissions
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>
    private lateinit var mediaPermissions: Array<String>
    // progress dialog
    private lateinit var progressDialog: ProgressDialog
    //Textrecog
    private lateinit var textRecognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scanner)
        //init UI Views
        inputImageBtn = findViewById(R.id.inputImageBtn)
        recognizeTextBtn = findViewById(R.id.recognizeTextBtn)
        imageIv = findViewById(R.id.imageIv)
        input = findViewById(R.id.editTextNumberDecimal)
        // init the arrays of permissions
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        // init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        // init textrecog
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        //handle click show input image dialog
        inputImageBtn.setOnClickListener{
            showInputImageDialog()
        }





        val toAddTransactionButton = findViewById<ImageButton>(R.id.toTransactionIB)
        toAddTransactionButton.setOnClickListener {


            val intent = Intent(this, AddTransactionActivity::class.java)
            // We need to check this! Doesn't play well with DecimalLimiter.
            intent.putExtra("Amount", input.text.toString())
            Log.d("!!!","amount before startActivity = ${input.text}")
            finish()
            startActivity(intent)

        }

        recognizeTextBtn.setOnClickListener {
            if (imageUri == null){
                //imageUri is null, we havent picked an image yet
                showToast("Pick image First...")
            }
            else{
                recognizeTextfromImage()
            }
        }

        val returnButton = findViewById<ImageButton>(R.id.returnIB)
        returnButton.setOnClickListener {
            finish()
        }
    }
    private fun recognizeTextfromImage() {
        // set message and show progress dialog
        progressDialog.setMessage("Preparing Image...")
        progressDialog.show()

        try {
            // prepare InputImage from image uri
            val inputImage = InputImage.fromFilePath(this,imageUri!!)
            //image prepared starting the recognizing process
            progressDialog.setMessage("Working on it...")

            val textTaskResult = textRecognizer.process(inputImage)
                .addOnSuccessListener { text ->

                    progressDialog.dismiss()
                    // get the recognized text
                    val inputT = text.text
                    // sets the recognized text to edit text
                    input.setText(inputT)

                }
                .addOnFailureListener { e->
                    // failed recognizing show reason in toast
                    progressDialog.dismiss()
                    showToast("Failed due to ${e.message}")

                }

        }
        catch (e: Exception){

            progressDialog.dismiss()
            // Exception while preparing InputImage show reason in toast
            showToast("Failed to prepare image due to ${e.message}")
        }
    }


    private fun showInputImageDialog() {
        // init PopupMenu
        val popupMenu = PopupMenu(this, inputImageBtn)

        popupMenu.menu.add(Menu.NONE, 1, 1, "CAMERA")
        popupMenu.menu.add(Menu.NONE, 2, 2, "GALLERY")


        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->

            val id = menuItem.itemId
            if (id == 1){

                if (checkCameraPermissions()){
                    pickImageCamera()
                }
                else{
                    requestCameraPermissions()
                }

            }

            else if(id == 2){
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }


            return@setOnMenuItemClickListener true

        }


    }

    private fun pickImageGallery(){
        // intent to pick image from gallery
        val intent = Intent(Intent.ACTION_PICK)
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    }



    private fun pickImageCamera(){
        //get the image data to store in media store
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Sample Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description")


        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        // intent to launch the camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }


    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            // here we get the image from camera
            if (result.resultCode == Activity.RESULT_OK){

                imageIv.setImageURI(imageUri)
            }
            else{
                //cancelled
                showToast("Cancelled....")

            }

        }






    private fun checkCameraPermissions() : Boolean{
        //checks camera permissions return true or false
        val cameraResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        return cameraResult && storageResult
    }



    private fun requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE)

    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // handle permissions result
        when(requestCode){
            CAMERA_REQUEST_CODE ->{

                if (grantResults.isNotEmpty()){

                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted && storageAccepted){
                        pickImageCamera()
                    }
                    else{
                        showToast("Permissions missing, check your settings")
                    }
                }

            }
            STORAGE_REQUEST_CODE ->{
                if (grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED


                    if (storageAccepted){
                        pickImageGallery()
                    }
                    else{
                        showToast("Permission missing, check your settings")
                    }
                }
            }
            MEDIA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val mediaAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (mediaAccepted) {
                        pickImageGallery()
                    } else {
                        showToast("Permission missing, check your settings")
                    }
                }
            }
        }

    }





    private fun showToast(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            imageUri = uri
            imageIv.setImageURI(imageUri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
}