# BÁO CÁO THỰC HÀNH LAB 2: THIẾT KẾ GIAO DIỆN PHẲNG XML & VIEWBINDING

**Môn học:** Lập trình ứng dụng di động  
**Trường:** Đại học Văn Lang (VLU)  
**Sinh viên thực hiện:** Trần Quang Minh  
**Mã số sinh viên (MSSV):** 2474802010241  
**Lớp:** CD22TT1  
**Mã nguồn GitHub:** `https://github.com/minhtran27042006-code/Lab2_Layout_ViewBinding_2474802010241`

---

## 1. MỤC TIÊU VÀ KẾT QUẢ ĐẠT ĐƯỢC

### 1.1. Mục tiêu
- Xây dựng giao diện phẳng tối ưu hiệu năng kết xuất (Flat View Hierarchy) bằng `ConstraintLayout` theo chuẩn Google Android Native.
- Áp dụng kỹ thuật `ViewBinding` thay thế hoàn toàn `findViewById`, đảm bảo Null-safety và Type-safety tại thời điểm biên dịch (Compile-time).
- Thiết kế giao diện phản hồi linh hoạt (Responsive UI) hoạt động trơn tru trên nhiều cấu hình: màn hình nhỏ, màn hình lớn, xoay ngang (`layout-land`), hỗ trợ đa ngôn ngữ (Tiếng Việt và Tiếng Anh `values-en`).
- Nắm vững quy trình quản lý phiên bản với Git theo chuẩn Conventional Commits.

### 1.2. Mức độ hoàn thành các yêu cầu

| Mã yêu cầu | Mô tả chi tiết | Trạng thái |
| :--- | :--- | :---: |
| **R1** | Màn hình Đăng nhập chuẩn wireframe: `imgLogo`, `tvTitle`, `edtEmail`, `edtPassword`, `btnLogin`, `tvStatus`. | **Đạt 100%** |
| **R2** | Kiểm tra dữ liệu đăng nhập (validate): để trống báo thiếu, email sai định dạng, mật khẩu < 6 ký tự. | **Đạt 100%** |
| **R3** | Đăng nhập hợp lệ hiển thị trạng thái và chuyển màn hình qua `ProfileActivity` kèm `Intent.putExtra`. | **Đạt 100%** |
| **R4** | Màn hình Hồ sơ (`activity_profile.xml`): Avatar tỉ lệ 1:1, Guideline 30%, Barrier nhãn thông tin, Chain nút bấm chia đều. | **Đạt 100%** |
| **R5** | 100% sử dụng `ConstraintLayout` + `ViewBinding`, không dùng `findViewById`, toàn bộ chuỗi nằm trong `strings.xml`. | **Đạt 100%** |
| **R6** | Khởi tạo kho Git hoàn chỉnh, commit rõ ràng theo chuẩn, sẵn sàng đẩy lên GitHub. | **Đạt 100%** |
| **Cấp 1** | Thêm mục "Quên mật khẩu?" với sự kiện Toast; hoàn thiện hỗ trợ song ngữ Tiếng Việt & Tiếng Anh (`values-en`). | **Đạt 100%** |
| **Cấp 2** | Hỗ trợ xoay ngang `res/layout-land/activity_login.xml` (chia 40% trái - 60% phải); CheckBox hiện/ẩn mật khẩu; Barrier cho trường Số điện thoại. | **Đạt 100%** |
| **Cấp 3** | Bọc layout bằng `ScrollView` (`fillViewport="true"`) chống tràn khi bàn phím ảo hiển thị. | **Đạt 100%** |

---

## 2. KIẾN TRÚC VÀ CÁC ĐIỂM KỸ THUẬT QUAN TRỌNG

### 2.1. Bố cục phẳng với ConstraintLayout
- **Giải quyết Double Taxation:** Thay vì lồng nhiều tầng `LinearLayout` (chi phí tính toán $O(2^d)$ khi đo kích thước), ứng dụng duy trì cây View độ sâu $d \approx 1$ giúp thuật toán Cassowary giải quyết đồ thị neo theo độ phức tạp tuyến tính $O(n)$, đảm bảo tốc độ khung hình mượt mà 60 FPS (< 16.6 ms/frame).
- **Quy tắc neo tối thiểu:** Mỗi phần tử giao diện đều được neo tối thiểu 2 điểm (1 ngang + 1 dọc).
- **Match Constraint (`0dp`):** Sử dụng `0dp` kết hợp neo 2 cạnh thay cho `match_parent` để các ô nhập và nút bấm giãn tự nhiên theo lề margin.
- **Tỉ lệ khung hình (Dimension Ratio):** Avatar được cấu hình `app:layout_constraintWidth_percent="0.35"` và `app:layout_constraintDimensionRatio="H,1:1"`, đảm bảo ảnh luôn vuông vắn và cân xứng trên mọi tỷ lệ màn hình.
- **Guideline & Barrier:**
  - `Guideline` dọc tại 30% giúp phân chia cột nhãn và giá trị.
  - `Barrier` phía sau các nhãn (`barrierDirection="end"`) giúp các giá trị dữ liệu luôn thẳng hàng sau nhãn dài nhất, kể cả khi đổi sang ngôn ngữ khác.
- **Horizontal Chain:** Hai nút "Chỉnh sửa" và "Đăng xuất" được xếp vào chuỗi ngang với kiểu `spread` và `layout_constraintHorizontal_weight="1"`, chia đều bề ngang màn hình.

### 2.2. Cơ chế ViewBinding
- Bật trong `app/build.gradle.kts`:
  ```kotlin
  buildFeatures {
      viewBinding = true
  }
  ```
- Android Gradle Plugin tự động sinh các lớp binding:
  - `activity_login.xml` $\rightarrow$ `ActivityLoginBinding`
  - `activity_profile.xml` $\rightarrow$ `ActivityProfileBinding`
- Khởi tạo an toàn trong Activity:
  ```kotlin
  binding = ActivityLoginBinding.inflate(layoutInflater)
  setContentView(binding.root)
  ```
- **Lợi ích vượt trội:**
  1. **Null Safety:** Không còn nguy cơ `NullPointerException` khi gõ sai ID.
  2. **Type Safety:** Tự động đúng kiểu dữ liệu (`Button`, `EditText`, `TextView`), loại bỏ hoàn toàn `ClassCastException`.
  3. **Build Speed:** Nhanh hơn DataBinding do không cần xử lý biểu thức phức tạp trong file XML.

---

## 3. CẤU TRÚC THƯ MỤC DỰ ÁN

```
Lab2_Layout_ViewBinding_2474802010241/
├── app/
│   ├── build.gradle.kts                      # Cấu hình Module :app và bật ViewBinding
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml           # Khai báo LoginActivity (LAUNCHER) & ProfileActivity
│           ├── java/vn/edu/vlu/lab2/
│           │   ├── LoginActivity.kt          # Xử lý logic đăng nhập, validation, chuyển Intent
│           │   └── ProfileActivity.kt        # Nhận dữ liệu email, xử lý các nút chức năng
│           └── res/
│               ├── layout/
│               │   ├── activity_login.xml    # Giao diện Đăng nhập màn hình dọc
│               │   └── activity_profile.xml  # Giao diện Hồ sơ cá nhân (Guideline, Barrier, Chain)
│               ├── layout-land/
│               │   └── activity_login.xml    # Giao diện Đăng nhập xoay ngang (Guideline 40%)
│               ├── values/
│               │   ├── colors.xml            # Bảng màu chuẩn
│               │   ├── strings.xml           # Tài nguyên chuỗi tiếng Việt chuẩn
│               │   └── themes.xml            # Chủ đề Material3
│               └── values-en/
│                   └── strings.xml           # Tài nguyên chuỗi tiếng Anh (Hỗ trợ đa ngôn ngữ)
├── build.gradle.kts                          # Cấu hình Gradle cấp Project
├── settings.gradle.kts                       # Khai báo pluginManagement & repos
├── gradle.properties                         # Cấu hình JVM & bộ nhớ build
└── README.md                                 # Báo cáo nghiệm thu & hướng dẫn
```

---

## 4. KẾT QUẢ KIỂM THỬ (TEST CASES)

| STT | Tình huống kiểm thử | Thao tác | Kết quả hiển thị | Đánh giá |
| :---: | :--- | :--- | :--- | :---: |
| 1 | **Để trống dữ liệu** | Bỏ trống email hoặc mật khẩu $\rightarrow$ Bấm "ĐĂNG NHẬP" | Hiện Toast: *"Vui lòng nhập email và mật khẩu"*; `tvStatus`: *"Trạng thái: Vui lòng điền đủ dữ liệu"*. | **PASS** |
| 2 | **Email sai định dạng** | Nhập `abc` vào Email, mật khẩu `123456` $\rightarrow$ Bấm nút | Hiển thị thông báo lỗi ngay tại ô Email: *"Email không hợp lệ"*. | **PASS** |
| 3 | **Mật khẩu quá ngắn** | Nhập email hợp lệ, mật khẩu `123` (< 6 ký tự) $\rightarrow$ Bấm nút | Hiển thị lỗi tại ô Password: *"Mật khẩu tối thiểu 6 ký tự"*. | **PASS** |
| 4 | **Đăng nhập hợp lệ** | Nhập `minh.2474802010241@vlu.edu.vn` và mật khẩu `123456` | Cập nhật dòng trạng thái và tự động chuyển sang `ProfileActivity` với Email được điền chính xác. | **PASS** |
| 5 | **Hiện/Ẩn mật khẩu** | Tích chọn CheckBox "Hiện mật khẩu" | Ký tự mật khẩu chuyển đổi linh hoạt giữa dạng dấu chấm tròn (`••••••`) và văn bản rõ ràng. | **PASS** |
| 6 | **Quên mật khẩu** | Nhấn vào dòng "Quên mật khẩu?" | Hiển thị Toast thông báo: *"Chức năng lấy lại mật khẩu đang được phát triển"*. | **PASS** |
| 7 | **Xoay ngang màn hình** | Xoay thiết bị sang chế độ Landscape | Giao diện tự động thích ứng: Logo + Tiêu đề nằm ở nửa trái (40%), form nhập liệu ở nửa phải (60%). | **PASS** |
| 8 | **Đăng xuất** | Bấm nút "Đăng xuất" tại màn hình Profile | Lệnh `finish()` được gọi, đóng ProfileActivity và quay lại màn hình Login. | **PASS** |

---

## 5. ĐÁP ÁN 10 CÂU HỎI ÔN TẬP LÝ THUYẾT

1. **View và ViewGroup khác nhau thế nào?**  
   - `View` là thành phần giao diện đơn lẻ trực tiếp hiển thị và tương tác với người dùng (`TextView`, `EditText`, `Button`).
   - `ViewGroup` là một View đặc biệt đóng vai trò thùng chứa để quản lý vị trí, kích thước và phân bổ các View con (`ConstraintLayout`, `LinearLayout`, `FrameLayout`).
2. **1dp bằng bao nhiêu px trên màn hình 320 dpi?**  
   - Áp dụng công thức $px = dp \times (dpi / 160) = 1 \times (320 / 160) = \mathbf{2\ px}$.
3. **Vì sao cỡ chữ dùng sp thay vì dp?**  
   - `sp` (Scale-independent Pixels) ngoài tính toán theo mật độ điểm ảnh của máy còn nhân thêm hệ số phóng to/thu nhỏ cỡ chữ trong phần Cài đặt trợ năng (Accessibility) của người dùng, giúp hỗ trợ người có thị lực kém.
4. **View trong ConstraintLayout thiếu neo dọc thì sao?**  
   - Android Studio sẽ cảnh báo lỗi thiếu constraint; khi chạy ứng dụng trên máy ảo/máy thật, View sẽ bị rơi tự do về tọa độ đỉnh $y = 0$ (mép trên cùng).
5. **0dp nghĩa là gì trong ConstraintLayout?**  
   - `0dp` đại diện cho giá trị `MATCH_CONSTRAINT`, có nghĩa là kích thước của View theo chiều đó sẽ tự động co giãn vừa khít giữa hai điểm neo đối xứng hai đầu (sau khi đã trừ margin).
6. **horizontal_bias = 0.3 có nghĩa gì?**  
   - View sẽ bị kéo lệch về phía Start (bên trái theo LTR): 30% khoảng trống còn lại nằm bên Start và 70% khoảng trống nằm bên End.
7. **File activity_user_profile.xml sinh ra lớp Binding nào?**  
   - Sinh ra lớp `ActivityUserProfileBinding` theo quy tắc PascalCase kết hợp hậu tố `Binding`.
8. **ViewBinding khắc phục những lỗi nào của findViewById?**  
   - Khắc phục 3 lỗi nghiêm trọng lúc chạy: gõ sai tên ID (bắt lỗi biên dịch), ép sai kiểu dữ liệu View (`ClassCastException`), và tham chiếu ID không tồn tại trong layout hiện tại (`NullPointerException`).
9. **Vì sao Fragment phải gán `_binding = null` trong `onDestroyView`?**  
   - Vòng đời giao diện của Fragment kết thúc tại `onDestroyView`, nhưng đối tượng Fragment vẫn có thể được giữ trong bộ nhớ (Backstack). Nếu không gán `null`, tham chiếu mạnh đến ViewBinding sẽ khiến Garbage Collector không thể thu hồi bộ nhớ của View, dẫn đến hiện tượng Rò rỉ bộ nhớ (Memory Leak).
10. **Vì sao không commit `local.properties` và thư mục `build/` lên Git?**  
    - `local.properties` chứa đường dẫn SDK máy cá nhân và có thể chứa API key bảo mật; thư mục `build/` chứa các tệp nhị phân sinh tự động rất nặng và làm ô nhiễm kho mã nguồn. Cả hai đã được liệt kê trong `.gitignore`.

---

## 6. HƯỚNG DẪN ĐẨY LÊN GITHUB VÀ NỘP BÀI

Để đẩy dự án lên GitHub cá nhân, mở terminal tại thư mục gốc của dự án và chạy các lệnh sau:

```bash
# 1. Khởi tạo Git và kiểm tra trạng thái
git status

# 2. Thêm Remote trỏ về repository trên GitHub cá nhân của bạn:
git remote add origin https://github.com/minhtran27042006-code/Lab2_Layout_ViewBinding_2474802010241.git

# 3. Đổi tên nhánh mặc định thành main
git branch -M main

# 4. Đẩy toàn bộ mã nguồn lên GitHub
git push -u origin main
```
