import SwiftUI

/// Photo from the API with the bundled asset of the same name as placeholder / offline fallback.
struct TipPhoto: View {
    let url: String?
    let fallback: String

    var body: some View {
        AppColor.canvasSecondary
            .overlay {
                if let url, let remote = URL(string: url) {
                    AsyncImage(url: remote) { phase in
                        if let image = phase.image {
                            image.resizable().scaledToFill()
                        } else {
                            fallbackImage
                        }
                    }
                } else {
                    fallbackImage
                }
            }
            .clipped()
    }

    @ViewBuilder
    private var fallbackImage: some View {
        if UIImage(named: fallback) != nil {
            Image(fallback).resizable().scaledToFill()
        }
    }
}
