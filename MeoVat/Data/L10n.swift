import Foundation

/// UI strings for the language picked in Settings. Vietnamese is the source language;
/// Chinese and Japanese currently fall back to English. Tip content itself is Vietnamese only.
struct L10n {
    let language: AppLanguage

    private func t(_ vi: String, _ en: String) -> String { language == .vi ? vi : en }

    var tabHome: String { t("Chủ đề", "Topics") }
    var tabSaved: String { t("Đã lưu", "Saved") }
    var tabSettings: String { t("Cài đặt", "Settings") }

    var homeTitle: String { t("Mẹo vặt", "Life hacks") }
    var homeSubtitle: String { t("Chọn một chủ đề để xem các mẹo vặt", "Pick a topic to browse its tips") }
    func categoryTitle(_ name: String) -> String { t("Mẹo \(name.lowercased())", "\(name) tips") }
    var searchPlaceholder: String { t("Tìm mẹo vặt", "Search tips") }
    var noResults: String { t("Không tìm thấy mẹo vặt nào", "No tips found") }

    var prepTitle: String { t("Danh sách chuẩn bị", "Preparation checklist") }
    var viewGuide: String { t("XEM HƯỚNG DẪN NGAY", "VIEW THE GUIDE") }
    func minutes(_ n: Int) -> String { t("\(n) PHÚT", "\(n) MIN") }
    func stepsUpper(_ n: Int) -> String { t("\(n) BƯỚC", "\(n) STEPS") }
    func stepsLower(_ n: Int) -> String { t("\(n) bước", "\(n) steps") }

    var saveTip: String { t("LƯU MẸO VẶT", "SAVE TIP") }
    var saveTipTitle: String { t("Lưu mẹo vặt", "Save tip") }
    var chooseList: String { t("Chọn danh sách để lưu", "Choose a list to save to") }
    var saveChanges: String { t("Lưu thay đổi", "Save changes") }
    var removeFromList: String { t("Bỏ khỏi danh sách", "Remove from list") }

    var savedTitle: String { t("Đã Lưu", "Saved") }
    var savedSubtitle: String { t("Danh sách các mẹo đã lưu", "Your saved tip lists") }
    var emptyList: String { t("Chưa có mẹo nào trong danh sách này", "No tips in this list yet") }
    var createList: String { t("Tạo danh sách", "Create list") }
    var listSettings: String { t("Cài đặt danh sách", "List settings") }
    var listName: String { t("Tên danh sách", "List name") }
    var listNamePlaceholder: String { t("Đặt tên cho danh sách", "Name your list") }
    var listDescription: String { t("Mô tả ngắn", "Short description") }
    var listDescriptionPlaceholder: String { t("Mô tả ngắn về danh sách", "A short description of the list") }
    var icon: String { t("Biểu tượng", "Icon") }
    var addIcon: String { t("Thêm biểu tượng cho danh sách", "Add an icon for the list") }
    var changeIcon: String { t("Ấn để thay đổi biểu tượng", "Tap to change the icon") }
    var create: String { t("Tạo mới", "Create") }
    var edit: String { t("Chỉnh sửa", "Edit") }
    var delete: String { t("Xoá danh sách", "Delete list") }
    var deleteConfirm: String { t("Xoá danh sách này? Các mẹo đã lưu trong đó sẽ bị bỏ.", "Delete this list? Tips saved in it will be removed.") }
    var cancel: String { t("Huỷ", "Cancel") }
    var chooseIcon: String { t("Chọn biểu tượng", "Choose an icon") }

    var sortTitle: String { t("Sắp xếp", "Sort") }
    var sortChoose: String { t("Chọn thứ tự hiển thị", "Choose display order") }
    var sortNewest: String { t("Theo thời gian (mới nhất)", "By time (newest first)") }
    var sortAlphabetical: String { t("Theo bảng chữ cái (A-Z)", "Alphabetical (A-Z)") }

    var settingsTitle: String { t("Cài đặt", "Settings") }
    var settingsSubtitle: String { t("Tùy chỉnh ứng dụng", "Customize the app") }
    var basicSettings: String { t("Cài đặt cơ bản", "Basic settings") }
    var notifications: String { t("Thông báo mẹo mới", "New tip notifications") }
    var languageTitle: String { t("Ngôn ngữ", "Language") }
    var about: String { t("Về ứng dụng", "About") }
    var chooseLanguage: String { t("Chọn ngôn ngữ hiển thị", "Choose display language") }
    var aboutBody: String {
        t("Mẹo Vặt tổng hợp những mẹo nhỏ hữu ích cho gia đình, cuộc sống, bếp núc và nhiều chủ đề khác. Lưu lại mẹo yêu thích vào danh sách riêng để xem lại bất cứ lúc nào.",
          "Mẹo Vặt collects handy little tips for family, everyday life, the kitchen and more. Save your favourites into lists to revisit them any time.")
    }
    var version: String { t("Phiên bản", "Version") }

    func languageName(_ l: AppLanguage) -> String {
        switch l {
        case .vi: t("Tiếng Việt", "Vietnamese")
        case .en: t("Tiếng Anh", "English")
        case .zh: t("Tiếng Trung", "Chinese")
        case .ja: t("Tiếng Nhật", "Japanese")
        }
    }
}
