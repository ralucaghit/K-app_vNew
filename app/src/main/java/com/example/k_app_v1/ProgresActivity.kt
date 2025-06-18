package com.example.k_app_v1

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProgresActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProgresAdapter
    private lateinit var backButton: ImageButton
    private var listaExercitii = mutableListOf<ProgresExercitiu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_progres)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        backButton = findViewById(R.id.backButton)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 coloane

        adapter = ProgresAdapter(listaExercitii)
        recyclerView.adapter = adapter

        backButton.setOnClickListener{
            finish()
        }
        // aici citești progresul din Firestore și adaugi la listaExercitii
        citesteProgresDinFirestore()
    }

    private fun citesteProgresDinFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // DEFINIȚIE: listă cu exerciții (nume, colecție, imagine)
        val listaExercitiiStatic = listOf(
            Triple("Învățăm litere", "progresLitere", R.drawable.background_button_all_letters),
            Triple("Completăm cuvinte", "progresExercitiiLiteraLipsa", R.drawable.background_button_missing_letter),
            Triple("Numărăm silabe", "progresExercitiiSilabe1", R.drawable.background_button_silabe1),
            Triple("Formăm silabe", "progresExercitiiSilabe2", R.drawable.background_button_silabe2),
            Triple("Învățăm numerele", "progresCifre", R.drawable.background_button_invatam_numerele),
            Triple("Numărăm", "progresNumarare", R.drawable.background_button_numarare),
            Triple("Urcăm și coborâm", "progresCrescDescresc", R.drawable.background_button_cresc_desc),
            Triple("Comparăm", "progresComparare", R.drawable.background_button_comparare),
            Triple("Adunăm", "progresAdunari", R.drawable.background_button_adunare),
            Triple("Scădem", "progresScaderi", R.drawable.background_button_scadere)
        )

        val numarNivelePerExercitiu = mapOf(
            "progresLitere" to 31,
            "progresExercitiiLiteraLipsa" to 8,
            "progresSilabe1" to 1,
            "progresSilabe2" to 1,
            "progresCifre" to 1,
            "progresNumarare" to 1,
            "progresCrescDescresc" to 1,
            "progresComparare" to 3,
            "progresAdunari" to 3,
            "progresScaderi" to 3
        )

        listaExercitii.clear() // Foarte important: golește lista, ca să nu se adune duplicate!

        val db = FirebaseFirestore.getInstance()

        // Variabilă pentru a ști când am citit toate rezultatele
        var numarExercitiiCitite = 0

        for ((nume, numeColectie, imagineResursa) in listaExercitiiStatic) {
            db.collection("copii").document(userId)
                .collection(numeColectie)
                .get()
                .addOnSuccessListener { snapshot ->
                    var procent = 0

                    // Dacă există document "finalizat", progresul e 100%
                    val docFinalizat = snapshot.documents.find { it.id == "finalizat" && it.getBoolean("terminat") == true }
                    if (docFinalizat != null) {
                        procent = 100
                    } else {
                        // Numără doar nivelele terminate
                        val numarNivele = numarNivelePerExercitiu[numeColectie] ?: 1
                        val niveleTerm = snapshot.documents.count { it.getBoolean("terminat") == true }
                        procent = ((niveleTerm * 100) / numarNivele)
                    }

                    listaExercitii.add(ProgresExercitiu(nume, procent, imagineResursa))

                    numarExercitiiCitite++
                    if (numarExercitiiCitite == listaExercitiiStatic.size) {
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener {
                    // Adaugă progres 0 și la eroare, ca să nu blochezi afișarea
                    listaExercitii.add(ProgresExercitiu(nume, 0, imagineResursa))
                    numarExercitiiCitite++
                    if (numarExercitiiCitite == listaExercitiiStatic.size) {
                        adapter.notifyDataSetChanged()
                    }
                }
        }
    }

}