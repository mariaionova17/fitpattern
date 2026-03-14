package cz.cvut.fit.bp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import runTestSolver
import testGeometry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        testGeometry()

        setContent {
            SolverPreviewScreen()
        }
    }
}

@Composable
fun SolverPreviewScreen() {
    val (problem, result) = runTestSolver()
    val piecesById = problem.pieces.associateBy { it.id }

    LayoutPreview(
        fabric = problem.fabrics.first().contour,
        piecesById = piecesById,
        placements = result.bestLayout.placed,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}

@Preview
@Composable
fun AppAndroidPreview() {
    SolverPreviewScreen()
}