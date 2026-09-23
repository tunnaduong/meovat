import SwiftUI

struct PrimaryButton: View {
    let title: String
    var icon: String? = nil
    var isEnabled = true
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if let icon {
                    AppIcon(name: icon, size: 22)
                }
                Text(title)
                    .font(AppFont.textMDSemibold)
            }
            .foregroundStyle(.white)
            .frame(maxWidth: .infinity)
            .frame(height: 56)
            .background(AppColor.primary.opacity(isEnabled ? 1 : 0.4), in: Capsule())
        }
        .buttonStyle(.plain)
        .disabled(!isEnabled)
    }
}

struct RadioIndicator: View {
    let selected: Bool

    var body: some View {
        ZStack {
            Circle()
                .fill(selected ? AppColor.primary : .white)
            if selected {
                Circle()
                    .fill(.white)
                    .frame(width: 10, height: 10)
            } else {
                Circle()
                    .stroke(AppColor.border, lineWidth: 1)
            }
        }
        .frame(width: 24, height: 24)
    }
}

struct FilterChip: View {
    let title: String
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(AppFont.textSMMedium)
                .foregroundStyle(selected ? .white : AppColor.heading)
                .padding(.horizontal, 16)
                .frame(height: 36)
                .background(selected ? AppColor.primary : AppColor.surface, in: RoundedRectangle(cornerRadius: 8))
                .overlay(RoundedRectangle(cornerRadius: 8).stroke(selected ? .clear : AppColor.border, lineWidth: 1))
        }
        .buttonStyle(.plain)
    }
}

struct CheckRow: View {
    let title: String
    let checked: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Group {
                    if checked {
                        AppIcon(name: "ic_check", size: 24)
                            .foregroundStyle(AppColor.primary)
                    } else {
                        Circle()
                            .fill(.white)
                            .overlay(Circle().stroke(AppColor.border, lineWidth: 1))
                    }
                }
                .frame(width: 24, height: 24)
                Text(title)
                    .font(AppFont.textMD)
                    .foregroundStyle(AppColor.heading)
                    .multilineTextAlignment(.leading)
                Spacer(minLength: 0)
            }
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

/// Small "Phân loại 1" outline badge.
struct TagBadge: View {
    let text: String

    var body: some View {
        Text(text)
            .font(AppFont.textXSMedium)
            .foregroundStyle(AppColor.primary)
            .padding(.horizontal, 12)
            .padding(.vertical, 4)
            .background(AppColor.surface, in: RoundedRectangle(cornerRadius: 6))
            .overlay(RoundedRectangle(cornerRadius: 6).stroke(AppColor.primary, lineWidth: 1))
    }
}

/// Grey rounded container listing selectable rows with inset dividers.
struct OptionList<Item: Identifiable, Row: View>: View {
    let items: [Item]
    @ViewBuilder var row: (Item) -> Row

    var body: some View {
        VStack(spacing: 0) {
            ForEach(Array(items.enumerated()), id: \.element.id) { index, item in
                row(item)
                if index < items.count - 1 {
                    Rectangle()
                        .fill(AppColor.divider)
                        .frame(height: 1)
                        .padding(.horizontal, 16)
                }
            }
        }
        .background(AppColor.canvasSecondary, in: RoundedRectangle(cornerRadius: 12))
    }
}

struct OptionRow: View {
    let emoji: String
    let title: String
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Text(emoji)
                    .font(.system(size: 20))
                Text(title)
                    .font(AppFont.textMD)
                    .foregroundStyle(selected ? AppColor.primary : AppColor.heading)
                Spacer(minLength: 0)
                RadioIndicator(selected: selected)
            }
            .padding(.horizontal, 16)
            .frame(height: 60)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

struct FormField: View {
    let label: String
    let placeholder: String
    @Binding var text: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(AppFont.textMDMedium)
                .foregroundStyle(AppColor.heading)
            TextField(placeholder, text: $text)
                .font(AppFont.textMD)
                .foregroundStyle(AppColor.heading)
                .tint(AppColor.primary)
                .padding(.horizontal, 16)
                .frame(height: 48)
                .background(AppColor.surface, in: RoundedRectangle(cornerRadius: 8))
                .overlay(RoundedRectangle(cornerRadius: 8).stroke(AppColor.border, lineWidth: 1))
        }
    }
}
