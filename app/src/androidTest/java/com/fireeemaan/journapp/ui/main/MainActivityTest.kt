package com.fireeemaan.journapp.ui.main

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.ui.story.StoryActivity
import com.fireeemaan.journapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityTest {

    private val emailValid: String = "aseptesting@gmail.com"
    private val passwordValid: String = "aseptesting"
    private val emailInvalid: String = "aseppresto@gmail.com"
    private val passwordInvalid: String = "asepprestoempuk"
    private val emailWrongFormat: String = "mulyono&gmail.com"
    private val passwordLessThan8: String = "1234567"


    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        Intents.release()
    }

    @Test
    fun test01_postLogin_Success_And_Logout_Sucess() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText(emailValid)
        )
        closeSoftKeyboard()

        onView(withId(R.id.ed_login_password)).perform(
            typeText(passwordValid)
        )
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())

        Thread.sleep(4000)

        Intents.intended(
            hasComponent(StoryActivity::class.java.name),
            Intents.times(2)
        )

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        onView(withText("Logout")).perform(click())
        onView(withText(R.string.logout)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(R.string.yes)).inRoot(isDialog()).perform(click())

        Intents.intended(
            hasComponent(MainActivity::class.java.name),
            Intents.times(1)
        )
    }

    @Test
    fun test02_postLogin_Failed() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText(emailInvalid)
        )
        closeSoftKeyboard()

        onView(withId(R.id.ed_login_password)).perform(
            typeText(passwordInvalid)
        )
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test03_postLogin_FieldEmpty() {
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))
    }

    @Test
    fun test04_postLogin_EmailWrongFormat() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText(emailWrongFormat)
        )
        closeSoftKeyboard()

        onView(withId(R.id.ed_login_password)).perform(
            typeText(passwordValid)
        )
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))
    }

    @Test
    fun test05_postLogin_PasswordLessThan8() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText(emailValid)
        )
        closeSoftKeyboard()

        onView(withId(R.id.ed_login_password)).perform(
            typeText(passwordLessThan8)
        )
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))
    }

    @Test
    fun test06_postLogin_PasswordLessThan8_And_EmailWrongFormat() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText(emailWrongFormat)
        )
        closeSoftKeyboard()

        onView(withId(R.id.ed_login_password)).perform(
            typeText(passwordLessThan8)
        )
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))
    }

    @Test
    fun test07_postLogin_Success_And_Logout_Cancel() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText(emailValid)
        )
        closeSoftKeyboard()

        onView(withId(R.id.ed_login_password)).perform(
            typeText(passwordValid)
        )
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())

        Intents.intended(
            hasComponent(StoryActivity::class.java.name),
            Intents.times(2)
        )

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        onView(withText("Logout")).perform(click())
        onView(withText(R.string.logout)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(R.string.no)).inRoot(isDialog()).perform(click())

        Intents.intended(
            hasComponent(MainActivity::class.java.name),
            Intents.times(0)
        )
    }


}