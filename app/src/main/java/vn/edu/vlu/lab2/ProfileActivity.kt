package vn.edu.vlu.lab2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.edu.vlu.lab2.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        // Nhận email truyền từ LoginActivity qua Intent
        val userEmail = intent.getStringExtra(EXTRA_EMAIL).orEmpty()
        if (userEmail.isNotEmpty()) {
            binding.tvValueEmail.text = userEmail
        }

        // Nút Đăng xuất: quay về màn hình Login
        binding.btnLogout.setOnClickListener {
            finish()
        }

        // Nút Chỉnh sửa: thông báo Toast
        binding.btnEdit.setOnClickListener {
            Toast.makeText(this, R.string.msg_profile_edit, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}
