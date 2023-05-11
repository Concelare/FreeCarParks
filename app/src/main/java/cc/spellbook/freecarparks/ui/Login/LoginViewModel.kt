package cc.spellbook.freecarparks.ui.Login

import android.widget.EditText
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    lateinit var email: EditText;
    lateinit var password: EditText;
}