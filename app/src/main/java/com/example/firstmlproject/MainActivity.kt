package com.example.firstmlproject

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnCamera = findViewById<Button>(R.id.btnCamera)

        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager)!= null){
                startActivityForResult(intent, 123)
            }else{
                Toast.makeText(this,"Oops Something went Wrong",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123 && resultCode == RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap
            if(bitmap != null){
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap?) {

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap!!,0)

        val result = detector.process(image)
                .addOnSuccessListener { faces ->
                    //Our face is successFully Detected
                    var resultText = " "
                    var i = 1
                    for(face in faces){
                        resultText = "Face Number $i" +
                                "\nSmile : ${face.smilingProbability?.times(100)}%" +
                                "\nLeft Eye Open : ${face.leftEyeOpenProbability?.times(100)}%" +
                                "\n Right Eye Open : ${face.rightEyeOpenProbability?.times(100)}%"
                        i++
                    }
                    if(faces.isEmpty()){
                        Toast.makeText(this, "No face Detected",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,"Something Went Wrong",Toast.LENGTH_SHORT).show()
                }
        }


    }