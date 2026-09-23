import SwiftUI

/// Hero image, overlapping summary card, preparation checklist and the "view guide" CTA.
struct TipInfoView: View {
    let tipId: String
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    @State private var showGuide = false

    private let heroHeight: CGFloat = 324

    var body: some View {
        let l = store.l10n
        if let tip = store.tip(id: tipId) {
            ZStack(alignment: .topLeading) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 24) {
                        Image(tip.hero)
                            .resizable()
                            .scaledToFill()
                            .frame(maxWidth: .infinity)
                            .frame(height: heroHeight)
                            .clipped()
                        summaryCard(tip, l)
                            .padding(.horizontal, 20)
                            .padding(.top, -88)
                        prepList(tip, l)
                            .padding(.horizontal, 20)
                    }
                    .padding(.bottom, 24)
                }
                .ignoresSafeArea(edges: .top)

                Button { dismiss() } label: {
                    AppIcon(name: "ic_chevron_left", size: 20)
                        .foregroundStyle(AppColor.primary)
                        .frame(width: 36, height: 36)
                        .background(.white, in: Circle())
                }
                .buttonStyle(.plain)
                .padding(.leading, 20)
                .padding(.top, 20)
            }
            .background(AppColor.canvas)
            .safeAreaInset(edge: .bottom, spacing: 0) {
                PrimaryButton(title: l.viewGuide, icon: "ic_book_open") { showGuide = true }
                    .padding(.horizontal, 20)
                    .padding(.top, 12)
                    .padding(.bottom, 8)
                    .background(AppColor.canvas)
            }
            .toolbar(.hidden, for: .navigationBar)
            .fullScreenCover(isPresented: $showGuide) {
                TipDetailView(tipId: tipId)
            }
        }
    }

    private func summaryCard(_ tip: Tip, _ l: L10n) -> some View {
        VStack(spacing: 12) {
            TagBadge(text: tip.tag)
            Text(tip.title)
                .font(AppFont.headingMD)
                .foregroundStyle(AppColor.heading)
                .multilineTextAlignment(.center)
            Text(tip.subtitle)
                .font(AppFont.textMD)
                .foregroundStyle(AppColor.caption)
                .multilineTextAlignment(.center)
            HStack(spacing: 12) {
                HStack(spacing: 8) {
                    AppIcon(name: "ic_clock", size: 20)
                        .foregroundStyle(AppColor.primary)
                    Text(l.minutes(tip.minutes))
                }
                Rectangle()
                    .fill(AppColor.border)
                    .frame(width: 1, height: 20)
                HStack(spacing: 8) {
                    AppIcon(name: "ic_list_checks", size: 20)
                        .foregroundStyle(AppColor.primary)
                    Text(l.stepsUpper(tip.steps.count))
                }
            }
            .font(AppFont.textSMMedium)
            .foregroundStyle(AppColor.heading)
            .padding(.top, 8)
        }
        .padding(16)
        .frame(maxWidth: .infinity)
        .card(cornerRadius: 16)
    }

    private func prepList(_ tip: Tip, _ l: L10n) -> some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(l.prepTitle)
                .font(AppFont.headingSM)
                .foregroundStyle(AppColor.primary)
            ForEach(Array(tip.prep.enumerated()), id: \.offset) { index, item in
                CheckRow(title: item, checked: store.isPrepChecked(tipId: tip.id, index: index)) {
                    withAnimation(.snappy(duration: 0.2)) {
                        store.togglePrep(tipId: tip.id, index: index)
                    }
                }
            }
        }
    }
}
