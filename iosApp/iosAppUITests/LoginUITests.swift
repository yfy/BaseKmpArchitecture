import XCTest

final class LoginUITests: XCTestCase {

    // These tests assert Turkish labels, so force Turkish regardless of the simulator language.
    private func launchApp() -> XCUIApplication {
        let app = XCUIApplication()
        app.launchArguments += ["-AppleLanguages", "(tr)", "-AppleLocale", "tr_TR"]
        app.launch()
        return app
    }

    private func passOnboardingIfNeeded(_ app: XCUIApplication) {
        let skip = app.buttons["Atla"]
        if skip.waitForExistence(timeout: 4) { skip.tap() }
    }

    private func login(_ app: XCUIApplication) {
        passOnboardingIfNeeded(app)
        let email = app.textFields["Email"]
        XCTAssertTrue(email.waitForExistence(timeout: 10))
        email.tap()
        email.typeText("demo@yfy.dev")
        let password = app.secureTextFields["Şifre"]
        password.tap()
        password.typeText("1234")
        app.buttons["Giriş yap"].tap()
    }

    func testLoginNavigatesToHome() throws {
        let app = launchApp()
        login(app)
        XCTAssertTrue(app.staticTexts["Giriş başarılı 🎉"].waitForExistence(timeout: 10))
    }

    // Regression: unless logout rebuilds the root via ContentView's sessionID, the retained
    // LoginViewModel keeps its old `user` and the second login never reaches Home.
    func testReloginAfterLogout() throws {
        let app = launchApp()
        login(app)
        XCTAssertTrue(app.staticTexts["Giriş başarılı 🎉"].waitForExistence(timeout: 10))

        app.buttons["Çıkış yap"].tap()
        let confirm = app.alerts.buttons["Evet"]
        XCTAssertTrue(confirm.waitForExistence(timeout: 5))
        confirm.tap()

        let email = app.textFields["Email"]
        XCTAssertTrue(email.waitForExistence(timeout: 10))
        email.tap(); email.typeText("demo@yfy.dev")
        let password = app.secureTextFields["Şifre"]
        password.tap(); password.typeText("1234")
        app.buttons["Giriş yap"].tap()

        XCTAssertTrue(app.staticTexts["Giriş başarılı 🎉"].waitForExistence(timeout: 10))
    }

    // Only the validation path is covered: XCUITest typeText into the two SecureFields is unreliable
    // because of autofill. The signup success path is covered by commonTest AuthFlowsTest.
    func testSignupNavigationAndValidation() throws {
        let app = launchApp()

        passOnboardingIfNeeded(app)
        app.buttons["Kayıt ol"].tap()
        XCTAssertTrue(app.textFields["signupFirstName"].waitForExistence(timeout: 5))
        app.buttons["signupTermsCheckbox"].tap()
        app.buttons["Kayıt ol"].tap()
        XCTAssertTrue(app.staticTexts["Ad ve soyad zorunlu"].waitForExistence(timeout: 5))
    }

    func testForgotPasswordFlow() throws {
        let app = launchApp()

        passOnboardingIfNeeded(app)
        app.buttons["Şifremi unuttum"].tap()
        let email = app.textFields["Email"]
        XCTAssertTrue(email.waitForExistence(timeout: 5))
        email.tap(); email.typeText("ada@yfy.dev")
        app.buttons["Sıfırlama bağlantısı gönder"].tap()

        XCTAssertTrue(app.staticTexts["Sıfırlama bağlantısı e-postana gönderildi"].waitForExistence(timeout: 10))
    }

    func testLanguageSwitchUpdatesUI() throws {
        let app = XCUIApplication()
        app.launchArguments = ["-AppleLanguages", "(tr)", "-AppleLocale", "tr_TR"]
        app.launch()
        login(app)

        let profile = app.buttons["Profil"]
        XCTAssertTrue(profile.waitForExistence(timeout: 10))
        profile.tap()
        let settingsButton = app.buttons["Ayarlar"]
        XCTAssertTrue(settingsButton.waitForExistence(timeout: 10))
        settingsButton.tap()

        let english = app.buttons["English"]
        XCTAssertTrue(english.waitForExistence(timeout: 5))
        english.tap()
        XCTAssertTrue(app.navigationBars["Settings"].waitForExistence(timeout: 5))

        app.buttons["Türkçe"].tap()
        XCTAssertTrue(app.navigationBars["Ayarlar"].waitForExistence(timeout: 5))
    }

    func testProfileAndSettingsFlow() throws {
        let app = launchApp()
        login(app)

        let profile = app.buttons["Profil"]
        XCTAssertTrue(profile.waitForExistence(timeout: 10))
        profile.tap()

        XCTAssertTrue(app.staticTexts["demo@yfy.dev"].waitForExistence(timeout: 10))

        app.buttons["Ayarlar"].tap()
        XCTAssertTrue(app.buttons["Koyu"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.switches["Bildirimler"].waitForExistence(timeout: 5))
    }
}
