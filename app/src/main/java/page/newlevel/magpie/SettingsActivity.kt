package page.newlevel.magpie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import page.newlevel.magpie.ui.theme.MagpieTheme

class SettingsActivity : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
//                MagpieTheme {
//                    Scaffold(
//                    ) { innerPadding ->
                        SettingsMainScreen(
//                            modifier = Modifier.padding(innerPadding)
                            modifier = Modifier.background(
                                colorResource(id = R.color.settings_bg),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(34.dp, 34.dp, 17.dp, 17.dp)
                            )
                        )
//                    }
//                }
            }
        }
    }

    @Preview
    @Composable
    private fun SettingsMainScreen(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings",
                modifier = Modifier.padding(18.dp),
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = colorResource(id = R.color.white)
            )
            Button(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.safe_setting_btn_bg),
                    contentColor = colorResource(id = R.color.white)
                ),
            ) {
                Text("Backup Notes to File")
            }
            Button(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.danger_setting_btn_bg),
                    contentColor = colorResource(id = R.color.white)
                ),
            ) {
                Text("Restore Notes from File")
            }
            Button(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.danger_setting_btn_bg),
                    contentColor = colorResource(id = R.color.white)
                ),
            ) {
                Text("Delete All Notes")
            }
        }
    }
    private fun CreateAndSaveDBBackup() {

    }

}