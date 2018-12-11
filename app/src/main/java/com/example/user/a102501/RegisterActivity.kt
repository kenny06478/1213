package com.example.user.a102501

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.internal.InternalTokenResult
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //註冊鈕
        register_button_register.setOnClickListener{
            performRegister()
        }

        already_hava_an_account_text_view.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            Log.d("RegisterActivity","Try to show login activity")
        }

        selectphoto_button_register.setOnClickListener {
            Log.d("RegisterActivity","try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity","Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    fun performRegister(){
        val email = email_editText_register.text.toString()
        val password = password_editText_register.text.toString()

        // 沒輸入信箱 驗證並顯示於用戶端
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Please enter text in email",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity","Email is :"+ email)
        Log.d("RegisterActivity","Password is : $password" )

        // Firebase     Create a user  with email and paasword
        // 新增資料到 Firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d("RegisterActivity","Successfully created use with uid :  ${it.result!!.user.uid}")

                    uploadImageToFirebaseStorage()
                }
                //偵測錯誤訊息 在軟體測試中
                .addOnFailureListener{
                    Log.d("RegisterActivity","Failed to create email: ${it.message}")
                    Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_LONG)
                            .show()
                }
    }
    //編輯的圖上傳到資料庫
    private fun uploadImageToFirebaseStorage(){
        if ( selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref =  FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity","Successfully uploaded image:${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("RegisterActivity","File Location : $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                            .addOnFailureListener{
                                //do some logging here
                            }
                }
    }

    //新增資料至DataBase
    private fun saveUserToFirebaseDatabase(profileImageUrl:String){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username_editText_register.text.toString(),profileImageUrl)
        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity","Finally we saved to Firebase Database")
                    val intent = Intent(this,LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
    }
}
class   User (val uid: String,val username:String,val profileImageUrl: String)
