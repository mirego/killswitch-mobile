import SwiftUI
import common

@main
struct iOSApp: App {
    @State private var url = ""
    @State private var key = ""
    @State private var version = ""
    @State private var language = ""
    @State private var customDialog = false
    @State private var viewData: KillswitchViewData? = nil

    var body: some Scene {
        WindowGroup {
            ZStack() {
                VStack(spacing: 16) {
                    Image("icon")
                        .resizable()
                        .frame(width: 128, height: 128)
                    TextField(
                        "Url",
                        text: $url
                    )
                    TextField(
                        "Key",
                        text: $key
                    )
                    TextField(
                        "Version",
                        text: $version
                    )
                    TextField(
                        "Language",
                        text: $language
                    )
                    Toggle("Custom dialog", isOn: $customDialog)
                    Button("Engage") {
                        Task {
                            do {
                                let viewData = try await IOSKillswitch().engage(key: key, version: version, language: language, url: url)

                                class Listener : KillswitchListener {
                                    func onAlert() {
                                        print("onAlert")
                                    }

                                    func onDialogShown() {
                                        print("onDialogShown")
                                    }

                                    func onKill() {
                                        print("onKill")
                                    }

                                    func onOk() {
                                        print("onOk")
                                    }


                                }

                                if (customDialog) {
                                    self.viewData = viewData
                                } else {
                                    IOSKillswitch().showDialog(viewData: viewData, listener: Listener())
                                }
                            }
                            catch {
                                print("Killswitch error: \(error)")
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
                .padding(32)
                .blur(radius: viewData != nil ? 5 : 0)

                if let viewData = viewData {
                    CustomDialog(viewData: viewData) {
                        self.viewData = nil
                    }
                }
            }
        }
    }
}
