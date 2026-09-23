import SwiftUI

struct AboutView: View {
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss

    private var version: String {
        let info = Bundle.main.infoDictionary
        let short = info?["CFBundleShortVersionString"] as? String ?? "1.0"
        let build = info?["CFBundleVersion"] as? String ?? "1"
        return "\(short) (\(build))"
    }

    var body: some View {
        let l = store.l10n
        VStack(spacing: 0) {
            SubPageHeader(title: l.about, onBack: { dismiss() })
            ScrollView {
                VStack(spacing: 16) {
                    AppIcon(name: "ic_bookmark_plus", size: 40)
                        .foregroundStyle(.white)
                        .frame(width: 88, height: 88)
                        .background(AppColor.primary, in: RoundedRectangle(cornerRadius: 20))
                    Text("Mẹo Vặt")
                        .font(AppFont.headingMD)
                        .foregroundStyle(AppColor.heading)
                    Text("\(l.version) \(version)")
                        .font(AppFont.textSM)
                        .foregroundStyle(AppColor.caption)
                    Text(l.aboutBody)
                        .font(AppFont.textMD)
                        .foregroundStyle(AppColor.body)
                        .multilineTextAlignment(.center)
                        .lineSpacing(4)
                        .padding(.top, 8)
                }
                .padding(.horizontal, 32)
                .padding(.top, 40)
                .padding(.bottom, 120)
                .frame(maxWidth: .infinity)
            }
        }
        .background(AppColor.canvas)
        .toolbar(.hidden, for: .navigationBar)
    }
}
