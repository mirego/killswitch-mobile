import SwiftUI
import common

@main
struct iOSApp: App {
    @State private var url: String = ""
    @State private var key: String = ""
    @State private var language: String = ""
    @State private var version: String = ""

    var body: some Scene {
        WindowGroup {
            VStack {
                TextField(
                    "Url",
                    text: $url
                )
                TextField(
                    "Key",
                    text: $key
                )
                TextField(
                    "Language",
                    text: $language
                )
                TextField(
                    "Version",
                    text: $version
                )
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
                            IOSKillswitch().showDialog(viewData: viewData, listener: Listener())
                        }
                        catch {
                        }
                    }
                }
            }
        }
    }
}
