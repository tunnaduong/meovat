import SwiftUI

/// "Tạo danh sách" / "Cài đặt danh sách" bottom sheet.
struct ListFormSheet: View {
    enum Mode {
        case create
        case edit(SavedList)
    }

    let mode: Mode
    var onCreate: ((SavedList) -> Void)? = nil
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    @State private var name = ""
    @State private var description = ""
    @State private var emoji: String?
    @State private var showEmojiPicker = false

    private var isEdit: Bool { if case .edit = mode { true } else { false } }

    var body: some View {
        let l = store.l10n
        SheetScaffold(icon: isEdit ? "ic_settings_2" : "ic_clipboard_list",
                      title: isEdit ? l.listSettings : l.createList) {
            FormField(label: l.listName, placeholder: l.listNamePlaceholder, text: $name)
            FormField(label: l.listDescription, placeholder: l.listDescriptionPlaceholder, text: $description)
            VStack(alignment: .leading, spacing: 8) {
                Text(l.icon)
                    .font(AppFont.textMDMedium)
                    .foregroundStyle(AppColor.heading)
                Button { showEmojiPicker = true } label: {
                    VStack(spacing: 12) {
                        if let emoji {
                            Text(emoji)
                                .font(.system(size: 24))
                                .frame(width: 36, height: 36)
                                .background(AppColor.surface, in: RoundedRectangle(cornerRadius: 8))
                                .overlay(RoundedRectangle(cornerRadius: 8).stroke(AppColor.primary, lineWidth: 1))
                        } else {
                            AppIcon(name: "ic_smile_plus", size: 24)
                                .foregroundStyle(AppColor.primary)
                                .frame(width: 36, height: 36)
                                .overlay(Circle().stroke(AppColor.primary, lineWidth: 1))
                        }
                        Text(emoji == nil ? l.addIcon : l.changeIcon)
                            .font(AppFont.textSM)
                            .foregroundStyle(AppColor.disabled)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 120)
                    .background(AppColor.surface, in: RoundedRectangle(cornerRadius: 12))
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColor.border, lineWidth: 1))
                }
                .buttonStyle(.plain)
            }
            PrimaryButton(title: isEdit ? l.saveChanges : l.create,
                          isEnabled: !name.trimmingCharacters(in: .whitespaces).isEmpty) {
                let trimmedName = name.trimmingCharacters(in: .whitespaces)
                let trimmedDescription = description.trimmingCharacters(in: .whitespaces)
                switch mode {
                case .create:
                    let created = store.createList(name: trimmedName, description: trimmedDescription, emoji: emoji)
                    onCreate?(created)
                case .edit(var list):
                    list.name = trimmedName
                    list.description = trimmedDescription
                    list.emoji = emoji
                    store.updateList(list)
                }
                dismiss()
            }
            .padding(.top, 8)
        }
        .onAppear {
            if case .edit(let list) = mode {
                name = list.name
                description = list.description
                emoji = list.emoji
            }
        }
        .sheet(isPresented: $showEmojiPicker) {
            EmojiPickerSheet(selected: $emoji)
        }
        .fittedSheet()
    }
}

struct EmojiPickerSheet: View {
    @Binding var selected: String?
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss

    private let emojis = ["🗒️", "🏥", "🚗", "🏠", "🍳", "🧹", "🧺", "🌿", "❤️", "🎁", "🧳", "👕",
                          "🍎", "🧴", "💡", "🛠️", "🐶", "🌸", "📚", "🎨", "☕️", "🧼", "🪴", "🚲"]

    var body: some View {
        SheetScaffold(icon: "ic_smile_plus", title: store.l10n.chooseIcon) {
            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: 6), spacing: 8) {
                ForEach(emojis, id: \.self) { emoji in
                    Button {
                        selected = emoji
                        dismiss()
                    } label: {
                        Text(emoji)
                            .font(.system(size: 28))
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(selected == emoji ? AppColor.primarySubtle : AppColor.canvasSecondary,
                                        in: RoundedRectangle(cornerRadius: 10))
                    }
                    .buttonStyle(.plain)
                }
            }
        }
        .fittedSheet()
    }
}
