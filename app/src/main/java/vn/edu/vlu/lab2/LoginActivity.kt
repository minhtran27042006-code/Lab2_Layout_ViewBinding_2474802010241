package vn.edu.vlu.lab2

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.edu.vlu.lab2.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        // Nút Đăng nhập
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        // CheckBox Hiện/Ẩn mật khẩu (Bài tập Cấp 2)
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            binding.edtPassword.transformationMethod =
                if (isChecked) null
                else PasswordTransformationMethod.getInstance()
            binding.edtPassword.setSelection(binding.edtPassword.text.length)
        }

        // Quên mật khẩu (Bài tập Cấp 1)
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, R.string.msg_forgot_password, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        // Xóa thông báo lỗi cũ
        binding.edtEmail.error = null
        binding.edtPassword.error = null

        when {
            email.isEmpty() || password.isEmpty() -> {
                Toast.makeText(this, R.string.msg_missing_info, Toast.LENGTH_SHORT).show()
                binding.tvStatus.setText(R.string.status_missing)
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.edtEmail.error = getString(R.string.err_email_invalid)
                binding.edtEmail.requestFocus()
            }
            password.length < 6 -> {
                binding.edtPassword.error = getString(R.string.err_password_short)
                binding.edtPassword.requestFocus()
            }
            else -> {
                binding.tvStatus.text = getString(R.string.status_login_ok, email)
                val intent = Intent(this, ProfileActivity::class.java).apply {
                    putExtra(ProfileActivity.EXTRA_EMAIL, email)
                }
                startActivity(intent)
            }
        }
    }
}
