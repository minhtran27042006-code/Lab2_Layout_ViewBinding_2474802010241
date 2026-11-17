# BÁO CÁO BÀI THỰC HÀNH
## ĐỀ TÀI: LAB 2 - THIẾT KẾ GIAO DIỆN PHẲNG XML & VIEWBINDING
**(PRACTICAL GUIDE: DESIGNING FLAT XML LAYOUT WITH CONSTRAINTLAYOUT & VIEWBINDING)**

---

### THÔNG TIN SINH VIÊN
- **Trường**: Đại học Văn Lang (VLU)
- **Họ và tên**: Trần Quang Minh
- **Mã số sinh viên (MSSV)**: 2474802010241
- **Lớp**: CD22TT1
- **Học phần**: Lập trình ứng dụng di động (Android Native - Kotlin)
- **Công cụ thực hiện**: Android Studio Ladybug/Koala, Kotlin DSL, Android Virtual Device (AVD API Level 34/37)
- **Repository GitHub**: `https://github.com/MinhTran274/Lab2_Layout_ViewBinding_2474802010241`

---

## 1. MỤC TIÊU BÀI HỌC (LESSON OBJECTIVES)
1. **Thiết kế giao diện phẳng (Flat View Hierarchy)**:
   - Sử dụng `ConstraintLayout` làm ViewGroup gốc, hạn chế triệt để lồng nhau nhiều tầng ViewGroup (tránh hiện tượng Double Taxation trong pha Measure với độ phức tạp $O(2^d)$).
   - Duy trì độ sâu cây View $d \approx 1$ giúp thuật toán Cassowary giải quyết đồ thị neo theo độ phức tạp tuyến tính $O(n)$, đảm bảo tốc độ khung hình 60 FPS (< 16.6 ms/frame).
2. **Khai thác toàn diện hệ thống neo (Constraints)**:
   - Nắm vững quy tắc neo tối thiểu 2 chiều: Horizontal (ngang) và Vertical (dọc).
   - Tối ưu kích thước linh hoạt bằng `0dp` (`MATCH_CONSTRAINT`) kết hợp Margin và Bias.
   - Ứng dụng các công cụ bố cục nâng cao: `Guideline`, `Barrier`, và `Chain` (chuỗi ngang/dọc với trọng số weight).
3. **Thao tác View an toàn tuyệt đối với ViewBinding**:
   - Thay thế hoàn toàn phương thức truyền thống `findViewById()`.
   - Đảm bảo **Null-safety** (không lo NullPointerException khi sai ID) và **Type-safety** (không lo ClassCastException khi ép sai kiểu View) tại thời điểm biên dịch (Compile-time).
4. **Xây dựng Responsive UI đa cấu hình**:
   - Tương thích tốt trên màn hình dọc (Portrait), xoay ngang (Landscape `layout-land`), và thích ứng khi người dùng phóng to cỡ chữ hệ thống (`sp`).
   - Hỗ trợ đa ngôn ngữ thông qua thư mục tài nguyên `res/values/strings.xml` (Tiếng Việt) và `res/values-en/strings.xml` (Tiếng Anh).

---

## 2. NỘI DUNG THỰC HIỆN CHI TIẾT

### PHẦN 1: KHỞI TẠO DỰ ÁN & KÍCH HOẠT VIEWBINDING
- **Template lựa chọn**: `Empty Views Activity` (chuẩn XML + Kotlin).
- **Cấu hình Package**: `vn.edu.vlu.lab2`.
- **Cấu hình `app/build.gradle.kts`**:
  ```kotlin
  android {
      namespace = "vn.edu.vlu.lab2"
      compileSdk {
          version = release(37)
      }
      defaultConfig {
          applicationId = "vn.edu.vlu.lab2"
          minSdk = 24
          targetSdk = 37
          versionCode = 1
          versionName = "1.0"
          testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      }
      buildFeatures {
          viewBinding = true
      }
  }
  ```

---

### PHẦN 2: THIẾT KẾ GIAO DIỆN ĐĂNG NHẬP (`activity_login.xml`)
Giao diện Đăng nhập gồm chuỗi neo dọc liên hoàn từ trên xuống dưới:
- **`ImageView` (`imgLogo`)**: 120dp $\times$ 120dp, margin top 32dp, căn giữa ngang màn hình.
- **`TextView` (`tvTitle`)**: Text "ĐĂNG NHẬP HỆ THỐNG", 22sp bold, neo dưới logo.
- **`EditText` (`edtEmail`)**: `0dp` (MATCH_CONSTRAINT), cao 50dp, inputType `textEmailAddress`, paddingStart/End 16dp.
- **`EditText` (`edtPassword`)**: `0dp`, cao 50dp, inputType `textPassword`, neo dưới ô Email.
- **`CheckBox` (`cbShowPassword`)**: Tùy chọn Hiện/Ẩn mật khẩu (Bài tập Cấp 2).
- **`Button` (`btnLogin`)**: `0dp`, cao 56dp, margin top 16dp, textSize 16sp bold.
- **`TextView` (`tvStatus`)**: Căn giữa, màu holo_blue_dark, hiển thị thông báo trạng thái.
- **`TextView` (`tvForgotPassword`)**: Dòng chữ "Quên mật khẩu?" có thể click (Bài tập Cấp 1).
- **Bọc ngoài bởi `ScrollView`** với `android:fillViewport="true"` nhằm chống che khuất nội dung khi bàn phím ảo xuất hiện (Bài tập Cấp 3).

---

### PHẦN 3: THIẾT KẾ GIAO DIỆN HỒ SƠ CÁ NHÂN (`activity_profile.xml`)
Giao diện Hồ sơ ứng dụng kỹ thuật cao cấp của ConstraintLayout:
1. **Avatar tỉ lệ 1:1**:
   - `app:layout_constraintWidth_percent="0.35"` (chiếm 35% bề ngang màn hình).
   - `app:layout_constraintDimensionRatio="H,1:1"` (chiều cao tự động suy ra bằng chiều rộng).
2. **Guideline 30% & Barrier**:
   - `Guideline` dọc tại 30% phân chia cột nhãn và cột giá trị.
   - `Barrier` với `app:barrierDirection="end"` gom các ID `tvLabelEmail, tvLabelMssv, tvLabelClass, tvLabelPhone` giúp cột giá trị luôn tự động dịch ra sau nhãn dài nhất (Bài tập Cấp 2).
3. **Căn thẳng dòng chữ với Baseline**:
   - Các giá trị sử dụng `app:layout_constraintBaseline_toBaselineOf` tương ứng với từng nhãn để đường chân chữ thẳng hàng tuyệt đối.
4. **Horizontal Chain dàn đều 2 nút**:
   - Nút "Chỉnh sửa" (`btnEdit`) và nút "Đăng xuất" (`btnLogout`) liên kết thành chuỗi ngang với `app:layout_constraintHorizontal_chainStyle="spread"` và `layout_constraintHorizontal_weight="1"`.

---

### PHẦN 4: LẬP TRÌNH XỬ LÝ LOGIC & VIEWBINDING

#### 1. Lớp `LoginActivity.kt`:
```kotlin
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
        binding.btnLogin.setOnClickListener { handleLogin() }

        // CheckBox Hiện/Ẩn mật khẩu
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            binding.edtPassword.transformationMethod =
                if (isChecked) null else PasswordTransformationMethod.getInstance()
            binding.edtPassword.setSelection(binding.edtPassword.text.length)
        }

        // Quên mật khẩu
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, R.string.msg_forgot_password, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

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
```

#### 2. Lớp `ProfileActivity.kt`:
```kotlin
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
        val userEmail = intent.getStringExtra(EXTRA_EMAIL).orEmpty()
        if (userEmail.isNotEmpty()) {
            binding.tvValueEmail.text = userEmail
        }

        binding.btnLogout.setOnClickListener { finish() }
        binding.btnEdit.setOnClickListener {
            Toast.makeText(this, R.string.msg_profile_edit, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}
```

---

## 3. KẾT QUẢ THỰC NGHIỆM TRÊN AVD (TEST CASES)

| STT | Tình huống kiểm thử | Thao tác nhập liệu | Kết quả mong đợi | Kết quả thực tế | Trạng thái |
|:---:|:---|:---|:---|:---|:---:|
| 1 | **Để trống dữ liệu** | Để trống cả 2 ô $\to$ Bấm ĐĂNG NHẬP | Toast: *"Vui lòng nhập email và mật khẩu"*; `tvStatus`: *"Trạng thái: Vui lòng điền đủ dữ liệu"* | Khớp 100% | **ĐẠT** |
| 2 | **Email sai định dạng** | Email: `quangminh`, Pass: `123456` | Báo lỗi tại ô Email: *"Email không hợp lệ"* | Khớp 100% | **ĐẠT** |
| 3 | **Mật khẩu < 6 ký tự** | Email: `minh@vlu.edu.vn`, Pass: `123` | Báo lỗi tại ô Mật khẩu: *"Mật khẩu tối thiểu 6 ký tự"* | Khớp 100% | **ĐẠT** |
| 4 | **Đăng nhập hợp lệ** | Email: `minh.2474802010241@vlu.edu.vn`, Pass: `123456` | Cập nhật dòng trạng thái và mở `ProfileActivity`, hiển thị đúng Email | Khớp 100% | **ĐẠT** |
| 5 | **Hiện/Ẩn mật khẩu** | Tích chọn CheckBox "Hiện mật khẩu" | Mật khẩu hiển thị dạng ký tự rõ thay vì dấu chấm tròn | Khớp 100% | **ĐẠT** |
| 6 | **Quên mật khẩu** | Click "Quên mật khẩu?" | Toast: *"Chức năng lấy lại mật khẩu đang được phát triển"* | Khớp 100% | **ĐẠT** |
| 7 | **Xoay ngang màn hình** | Xoay thiết bị sang chế độ Landscape | Giao diện nạp từ `layout-land`, chia 40% trái (logo, tiêu đề) và 60% phải (form) | Khớp 100% | **ĐẠT** |
| 8 | **Đăng xuất Profile** | Nhấn nút "Đăng xuất" | Đóng màn hình Profile bằng `finish()` và trở lại Login | Khớp 100% | **ĐẠT** |

---

## 4. CÂU HỎI VẤN ĐÁP BẢO VỆ BÀI THỰC HÀNH (10 CÂU HỎI CHUẨN)

1. **Câu hỏi: View và ViewGroup khác nhau thế nào?**  
   - **Trả lời**: `View` là thành phần hiển thị đơn lẻ cơ bản của giao diện (`TextView`, `EditText`, `Button`). `ViewGroup` là một View đặc biệt đóng vai trò thùng chứa để gom nhóm, sắp xếp vị trí và điều phối các View con (`ConstraintLayout`, `LinearLayout`, `FrameLayout`).

2. **Câu hỏi: 1dp bằng bao nhiêu px trên màn hình 320 dpi?**  
   - **Trả lời**: Theo công thức $px = dp \times (dpi / 160) = 1 \times (320 / 160) = \mathbf{2\ px}$.

3. **Câu hỏi: Vì sao cỡ chữ dùng sp thay vì dp?**  
   - **Trả lời**: `sp` (Scale-independent Pixels) ngoài tính toán theo mật độ điểm ảnh (dpi) còn tự động co giãn theo tỉ lệ phóng to/thu nhỏ cỡ chữ trong phần Cài đặt trợ năng (Accessibility) của hệ điều hành, hỗ trợ người dùng có thị lực kém.

4. **Câu hỏi: View trong ConstraintLayout thiếu neo dọc thì sao?**  
   - **Trả lời**: Android Studio sẽ báo lỗi biên dịch/cảnh báo trong Design view. Khi chạy trên thiết bị, View sẽ bị rơi tự do về góc trên cùng bên trái tại tọa độ $y = 0$, gây đè lấp lên các View khác.

5. **Câu hỏi: 0dp nghĩa là gì trong ConstraintLayout?**  
   - **Trả lời**: `0dp` đại diện cho `MATCH_CONSTRAINT`, có nghĩa là kích thước của View theo chiều đó hoàn toàn do khoảng cách giữa hai điểm neo đối xứng hai đầu quyết định (sau khi đã tính trừ margin).

6. **Câu hỏi: horizontal_bias = 0.3 có nghĩa gì?**  
   - **Trả lời**: View được neo cả 2 đầu trái/phải sẽ bị kéo lệch về phía Start: 30% khoảng trống còn lại phân bổ bên Start và 70% phân bổ bên End.

7. **Câu hỏi: File activity_user_profile.xml sinh ra lớp Binding nào?**  
   - **Trả lời**: Sinh ra lớp `ActivityUserProfileBinding` theo quy tắc chuyển đổi snake_case sang PascalCase kèm hậu tố `Binding`.

8. **Câu hỏi: ViewBinding khắc phục những lỗi nào của findViewById?**  
   - **Trả lời**: Khắc phục 3 lỗi nghiêm trọng lúc runtime: gõ sai ID (bắt lỗi lúc compile), ép sai kiểu View (`ClassCastException`), và tham chiếu ID không tồn tại trong layout hiện tại (`NullPointerException`).

9. **Câu hỏi: Vì sao Fragment phải gán `_binding = null` trong `onDestroyView`?**  
   - **Trả lời**: Fragment có 2 vòng đời: Fragment instance và View lifecycle. Khi Fragment vào Backstack, giao diện bị hủy qua `onDestroyView` nhưng đối tượng Fragment vẫn tồn tại. Nếu không hủy binding, tham chiếu mạnh sẽ ngăn Garbage Collector thu hồi vùng nhớ của cây View, gây ra Rò rỉ bộ nhớ (Memory Leak).

10. **Câu hỏi: Vì sao không commit `local.properties` và thư mục `build/` lên Git?**  
    - **Trả lời**: `local.properties` chứa đường dẫn tuyệt đối SDK máy cá nhân và API keys nhạy cảm; thư mục `build/` chứa file nhị phân tạm sinh tự động rất nặng và làm ô nhiễm repository.

---

## 5. HƯỚNG DẪN ĐẨY LÊN GITHUB & BẰNG CHỨNG COMMIT

### 1. Chuỗi Commit theo chuẩn Conventional Commits:
```bash
029e938 docs: them README huong dan va bao cao nghiem thu
105f4b2 feat: bo sung che do xoay ngang layout-land va hien mat khau
7fdbf15 feat: them man hinh Profile responsive va Guideline Barrier
a577e36 feat: xu ly validate email va mat khau
e68c6af feat: thiet ke giao dien dang nhap bang ConstraintLayout
55263e1 chore: khoi tao project va bat ViewBinding
```

### 2. Lệnh đẩy mã nguồn lên GitHub:
```bash
git remote add origin git@github.com:MinhTran274/Lab2_Layout_ViewBinding_2474802010241.git
git branch -M main
git push -u origin main
```

---
*Báo cáo được hoàn thành bởi sinh viên: **Trần Quang Minh (MSSV: 2474802010241)***.
