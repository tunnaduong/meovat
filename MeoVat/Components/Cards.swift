import SwiftUI

/// White card with 1px border, 12pt radius and the "Raised" shadow.
struct CardStyle: ViewModifier {
    var cornerRadius: CGFloat = 12

    func body(content: Content) -> some View {
        content
            .background(AppColor.surface, in: RoundedRectangle(cornerRadius: cornerRadius))
            .overlay(RoundedRectangle(cornerRadius: cornerRadius).stroke(AppColor.border, lineWidth: 1))
            .shadow(color: .black.opacity(0.05), radius: 4, x: 0, y: 4)
    }
}

extension View {
    func card(cornerRadius: CGFloat = 12) -> some View { modifier(CardStyle(cornerRadius: cornerRadius)) }
}

struct CategoryCard: View {
    let category: TipCategory

    var body: some View {
        HStack(spacing: 12) {
            Text(category.emoji)
                .font(.system(size: 36))
            VStack(alignment: .leading, spacing: 0) {
                Text(category.name)
                    .font(AppFont.textLGMedium)
                    .foregroundStyle(AppColor.heading)
                Text(category.description)
                    .font(AppFont.textSM)
                    .foregroundStyle(AppColor.caption)
                    .lineLimit(1)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            AppIcon(name: "ic_arrow_right", size: 24)
                .foregroundStyle(AppColor.primary)
        }
        .padding(.horizontal, 16)
        .frame(height: 92)
        .card()
    }
}

struct TipCard<Menu: View>: View {
    let tip: Tip
    @ViewBuilder var menu: Menu

    var body: some View {
        HStack(spacing: 12) {
            Image(tip.image)
                .resizable()
                .scaledToFill()
                .frame(width: 64, height: 84)
                .clipShape(RoundedRectangle(cornerRadius: 8))
            VStack(alignment: .leading, spacing: 4) {
                Text(tip.title)
                    .font(AppFont.textMDMedium)
                    .foregroundStyle(AppColor.heading)
                    .lineLimit(2)
                Text(tip.subtitle)
                    .font(AppFont.textSM)
                    .foregroundStyle(AppColor.caption)
                    .lineLimit(1)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            SwiftUI.Menu {
                menu
            } label: {
                AppIcon(name: "ic_more_vertical", size: 20)
                    .foregroundStyle(AppColor.caption)
                    .frame(width: 36, height: 36)
                    .contentShape(Rectangle())
            }
        }
        .padding(6)
        .padding(.trailing, 4)
        .frame(height: 96)
        .card()
    }
}

struct SavedListCard: View {
    let list: SavedList

    var body: some View {
        HStack(spacing: 12) {
            Text(list.emoji ?? "📁")
                .font(.system(size: 36))
            VStack(alignment: .leading, spacing: 0) {
                Text(list.name)
                    .font(AppFont.textLGMedium)
                    .foregroundStyle(AppColor.heading)
                Text(list.description)
                    .font(AppFont.textSM)
                    .foregroundStyle(AppColor.caption)
                    .lineLimit(1)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            Text("\(list.tipIds.count)")
                .font(AppFont.textSMMedium)
                .foregroundStyle(AppColor.primary)
                .frame(width: 32, height: 32)
                .background(AppColor.primarySubtle, in: Circle())
        }
        .padding(.horizontal, 16)
        .frame(height: 92)
        .card()
    }
}
