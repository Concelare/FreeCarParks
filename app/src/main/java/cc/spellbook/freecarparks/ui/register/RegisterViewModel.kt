package cc.spellbook.freecarparks.ui.register

import android.widget.EditText
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    lateinit var email: EditText;
    lateinit var password: EditText;
}