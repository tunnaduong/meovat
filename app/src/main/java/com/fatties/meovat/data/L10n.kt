package com.fatties.meovat.data

/**
 * UI strings for the language picked in Settings. Vietnamese is the source language;
 * Chinese and Japanese currently fall back to English. Tip content itself is Vietnamese only.
 */
class L10n(private val language: AppLanguage) {
    private fun t(vi: String, en: String) = if (language == AppLanguage.VI) vi else en

    val tabHome get() = t("Chủ đề", "Topics")
    val tabSaved get() = t("Đã lưu", "Saved")
    val tabSettings get() = t("Cài đặt", "Settings")

    val homeTitle get() = t("Mẹo vặt", "Life hacks")
    val homeSubtitle get() = t("Chọn một chủ đề để xem các mẹo vặt", "Pick a topic to browse its tips")
    fun categoryTitle(name: String) = t("Mẹo ${name.lowercase()}", "$name tips")
    val searchPlaceholder get() = t("Tìm mẹo vặt", "Search tips")
    val noResults get() = t("Không tìm thấy mẹo vặt nào", "No tips found")

    val prepTitle get() = t("Danh sách chuẩn bị", "Preparation checklist")
    val viewGuide get() = t("XEM HƯỚNG DẪN NGAY", "VIEW THE GUIDE")
    fun minutes(n: Int) = t("$n PHÚT", "$n MIN")
    fun stepsUpper(n: Int) = t("$n BƯỚC", "$n STEPS")
    fun stepsLower(n: Int) = t("$n bước", "$n steps")

    val saveTip get() = t("LƯU MẸO VẶT", "SAVE TIP")
    val saveTipTitle get() = t("Lưu mẹo vặt", "Save tip")
    val chooseList get() = t("Chọn danh sách để lưu", "Choose a list to save to")
    val saveChanges get() = t("Lưu thay đổi", "Save changes")
    val removeFromList get() = t("Bỏ khỏi danh sách", "Remove from list")

    val savedTitle get() = t("Đã Lưu", "Saved")
    val savedSubtitle get() = t("Danh sách các mẹo đã lưu", "Your saved tip lists")
    val emptyList get() = t("Chưa có mẹo nào trong danh sách này", "No tips in this list yet")
    val createList get() = t("Tạo danh sách", "Create list")
    val listSettings get() = t("Cài đặt danh sách", "List settings")
    val listName get() = t("Tên danh sách", "List name")
    val listNamePlaceholder get() = t("Đặt tên cho danh sách", "Name your list")
    val listDescription get() = t("Mô tả ngắn", "Short description")
    val listDescriptionPlaceholder get() = t("Mô tả ngắn về danh sách", "A short description of the list")
    val icon get() = t("Biểu tượng", "Icon")
    val addIcon get() = t("Thêm biểu tượng cho danh sách", "Add an icon for the list")
    val changeIcon get() = t("Ấn để thay đổi biểu tượng", "Tap to change the icon")
    val create get() = t("Tạo mới", "Create")
    val edit get() = t("Chỉnh sửa", "Edit")
    val delete get() = t("Xoá danh sách", "Delete list")
    val deleteConfirm get() = t("Xoá danh sách này? Các mẹo đã lưu trong đó sẽ bị bỏ.", "Delete this list? Tips saved in it will be removed.")
    val cancel get() = t("Huỷ", "Cancel")
    val chooseIcon get() = t("Chọn biểu tượng", "Choose an icon")

    val sortTitle get() = t("Sắp xếp", "Sort")
    val sortChoose get() = t("Chọn thứ tự hiển thị", "Choose display order")
    val sortNewest get() = t("Theo thời gian (mới nhất)", "By time (newest first)")
    val sortAlphabetical get() = t("Theo bảng chữ cái (A-Z)", "Alphabetical (A-Z)")

    val settingsTitle get() = t("Cài đặt", "Settings")
    val settingsSubtitle get() = t("Tùy chỉnh ứng dụng", "Customize the app")
    val basicSettings get() = t("Cài đặt cơ bản", "Basic settings")
    val notifications get() = t("Thông báo mẹo mới", "New tip notifications")
    val languageTitle get() = t("Ngôn ngữ", "Language")
    val about get() = t("Về ứng dụng", "About")
    val chooseLanguage get() = t("Chọn ngôn ngữ hiển thị", "Choose display language")
    val aboutBody get() = t(
        "Mẹo Vặt tổng hợp những mẹo nhỏ hữu ích cho gia đình, cuộc sống, bếp núc và nhiều chủ đề khác. Lưu lại mẹo yêu thích vào danh sách riêng để xem lại bất cứ lúc nào.",
        "Mẹo Vặt collects handy little tips for family, everyday life, the kitchen and more. Save your favourites into lists to revisit them any time.",
    )
    val version get() = t("Phiên bản", "Version")
    val offline get() = t("Không kết nối được máy chủ – đang dùng dữ liệu trên máy", "Can't reach the server – showing on-device data")

    fun languageName(l: AppLanguage) = when (l) {
        AppLanguage.VI -> t("Tiếng Việt", "Vietnamese")
        AppLanguage.EN -> t("Tiếng Anh", "English")
        AppLanguage.ZH -> t("Tiếng Trung", "Chinese")
        AppLanguage.JA -> t("Tiếng Nhật", "Japanese")
    }
}
