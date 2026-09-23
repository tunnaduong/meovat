import SwiftUI

/// Step-by-step guide presented full screen; steps expand as an accordion.
struct TipDetailView: View {
    let tipId: String
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    @State private var expanded: Set<Int> = [0]
    @State private var showSave = false

    var body: some View {
        let l = store.l10n
        if let tip = store.tip(id: tipId) {
            VStack(spacing: 0) {
                HStack {
                    Button { dismiss() } label: {
                        AppIcon(name: "ic_x", size: 20)
                            .foregroundStyle(AppColor.primary)
                            .frame(width: 36, height: 36)
                    }
                    .buttonStyle(.plain)
                    Spacer()
                    Button { showSave = true } label: {
                        HStack(spacing: 8) {
                            AppIcon(name: "ic_square_plus", size: 18)
                            Text(l.saveTip)
                                .font(AppFont.textSMSemibold)
                        }
                        .foregroundStyle(AppColor.primary)
                        .padding(.horizontal, 12)
                        .frame(height: 36)
                        .overlay(RoundedRectangle(cornerRadius: 8).stroke(AppColor.primary, lineWidth: 1))
                    }
                    .buttonStyle(.plain)
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 20)
                .background(AppColor.canvas)
                Rectangle()
                    .fill(AppColor.divider)
                    .frame(height: 1)

                ScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        VStack(alignment: .leading, spacing: 12) {
                            HStack(spacing: 8) {
                                AppIcon(name: "ic_clock", size: 20)
                                    .foregroundStyle(AppColor.primary)
                                Text(l.minutes(tip.minutes))
                                    .font(AppFont.textSMMedium)
                                    .foregroundStyle(AppColor.caption)
                            }
                            Text(tip.title)
                                .font(AppFont.headingMD)
                                .foregroundStyle(AppColor.heading)
                            HStack(spacing: 8) {
                                AppIcon(name: "ic_list_checks", size: 20)
                                Text(l.stepsLower(tip.steps.count))
                                    .font(AppFont.textMDMedium)
                            }
                            .foregroundStyle(AppColor.primary)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 8)
                            .background(AppColor.primarySubtle, in: RoundedRectangle(cornerRadius: 8))
                        }
                        .padding(20)
                        .padding(.bottom, 4)

                        ForEach(Array(tip.steps.enumerated()), id: \.offset) { index, step in
                            stepSection(index: index, step: step)
                        }
                    }
                    .padding(.bottom, 40)
                }
                .background(AppColor.canvasSecondary)
            }
            .background(AppColor.canvas)
            .sheet(isPresented: $showSave) {
                SaveToListSheet(tip: tip)
            }
        }
    }

    private func stepSection(index: Int, step: TipStep) -> some View {
        let isOpen = expanded.contains(index)
        return VStack(alignment: .leading, spacing: 0) {
            Button {
                withAnimation(.snappy(duration: 0.25)) {
                    if isOpen { expanded.remove(index) } else { expanded.insert(index) }
                }
            } label: {
                HStack(spacing: 16) {
                    Text("\(index + 1)")
                        .font(AppFont.textLGMedium)
                        .foregroundStyle(.white)
                        .frame(width: 40, height: 40)
                        .background(AppColor.primary, in: Circle())
                    Text(step.title)
                        .font(AppFont.headingSM)
                        .foregroundStyle(AppColor.heading)
                        .multilineTextAlignment(.leading)
                    Spacer(minLength: 8)
                    AppIcon(name: "ic_chevron_down", size: 20)
                        .foregroundStyle(AppColor.primary)
                        .rotationEffect(.degrees(isOpen ? 180 : 0))
                }
                .padding(.horizontal, 20)
                .frame(minHeight: 88)
                .background(AppColor.canvas)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)

            if isOpen {
                if let image = step.image {
                    Image(image)
                        .resizable()
                        .scaledToFill()
                        .frame(maxWidth: .infinity)
                        .frame(height: 260)
                        .clipped()
                }
                Text(step.body)
                    .font(AppFont.textMD)
                    .foregroundStyle(AppColor.body)
                    .lineSpacing(4)
                    .padding(20)
            }
        }
        .clipped()
    }
}
