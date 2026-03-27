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
import cz.cvut.fit.bp.imageRecognition.OpenCvTestScreen
import domain.geometry.setSeamAllowance
import runTestSolver
import testGeometry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        System.loadLibrary("opencv_java4")

        //testGeometry()

        setContent {
            OpenCvTestScreen()
            //SolverPreviewScreen()
        }
    }
}

@Composable
fun SolverPreviewScreen() {
    val (problem, result) = runTestSolver()
    val piecesById = problem.pieces.associateBy { it.id }

    val seamAllowance = 10.0

    val piecesByIdSeamAllowance = problem.pieces
        .map { piece ->
            piece.copy(shape = piece.shape.setSeamAllowance(seamAllowance))
        }
        .associateBy { it.id }

    LayoutPreview(
        fabric = problem.fabrics.first().contour,
        piecesById = piecesById,
        piecesByIdSeamAllowance = piecesByIdSeamAllowance,
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
