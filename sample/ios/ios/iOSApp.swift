import SwiftUI
import common

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView().onAppear {
                Task {
                    let killswitch = IOSKillswitch()
                    let viewData = try await killswitch.engage(key: "ce087ed5f2196edc982452c22dc5ef6b89496bec9aad8da5a94582b41c54a8ba", version: "0.0.1", language: "en", url: "https://mirego-killswitch-qa.herokuapp.com/killswitch")
                    killswitch.showDialog(viewData: viewData)
                }
            }
        }
    }
}
