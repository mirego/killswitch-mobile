import SwiftUI
import common

struct CustomDialog: View {
    let viewData: KillswitchViewData
    let onCancelled: () -> Void

    var body: some View {
        VStack() {
            Spacer()
            Text(viewData.message)
                .foregroundColor(Color.white)
            Spacer()
            VStack(spacing: 16) {
                ForEach(viewData.buttons, id: \.self) { button in
                    let closeAction = {
                        if (viewData.isCancelable) {
                            onCancelled()
                        }
                    }

                    Button(button.title, action: {
                        switch button.action {
                        case is KillswitchButtonActionClose:
                            closeAction()
                        case let action as KillswitchButtonActionNavigateToUrl:
                            if let url = URL(string: action.url) {
                                UIApplication.shared.open(url, options: [:], completionHandler: nil)
                            }
                            closeAction()
                        default:
                            break
                        }
                    })
                }
            }
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.black.opacity(0.85))
        .ignoresSafeArea()
    }
}
