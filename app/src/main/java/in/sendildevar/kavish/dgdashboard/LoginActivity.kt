package `in`.sendildevar.kavish.dgdashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


@Suppress("NAME_SHADOWING")
class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val account = GoogleSignIn.getLastSignedInAccount(this)
        ////Log.d("dgdashboard.login",account.toString())
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 9001)
        }
    }
    private fun sendToken(token: String) {
        //Log.d("dgdashboard.login",token)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9001) {
            val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                signInButton.isEnabled=false
                signInButton.isClickable=false
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                Toast.makeText(applicationContext,"Logged in as " + account.displayName,Toast.LENGTH_SHORT).show()
                sendToken(account.idToken.toString())
                //Log.w("dgdashboard.login", "signInResult:logged in" + account.email)
            } catch (e: ApiException) {
                //Log.w("dgdashboard.login", "signInResult:failed code=" + e.statusCode)
            }
        }
    }
}