package cz.cvut.fit.bp.projectCreation

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SelectImageScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun clicking_test_image_buttons_calls_callback_with_expected_filename() {
        var selectedImage: String? = null

        composeRule.setContent {
            MaterialTheme {
                SelectImageScreen(
                    onBack = {},
                    onPickTestImage = { selectedImage = it },
                    onPickImageFromGallery = {}
                )
            }
        }

        composeRule.onNodeWithText("Use test_image.jpg").performClick()
        assertEquals("test_image.jpg", selectedImage)

        composeRule.onNodeWithText("Use test_image2.jpg").performClick()
        assertEquals("test_image2.jpg", selectedImage)
    }

    @Test
    fun clicking_back_calls_callback() {
        var backClicked = false

        composeRule.setContent {
            MaterialTheme {
                SelectImageScreen(
                    onBack = { backClicked = true },
                    onPickTestImage = {},
                    onPickImageFromGallery = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertEquals(true, backClicked)
    }

    @Test
    fun shows_instruction_and_buttons() {
        composeRule.setContent {
            MaterialTheme {
                SelectImageScreen(
                    onBack = {},
                    onPickTestImage = {},
                    onPickImageFromGallery = {}
                )
            }
        }

        composeRule.onNodeWithText("Pick image from gallery").assertIsDisplayed()
        composeRule.onNodeWithText("Use test_image.jpg").assertIsDisplayed()
        composeRule.onNodeWithText("Use test_image2.jpg").assertIsDisplayed()
    }
}
