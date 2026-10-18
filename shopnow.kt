abstract class Produit(
    val code: String,
    val nom: String,
    var prixHt: Double,
    stock: Int
) {
    var stock: Int = stock
        private set

    abstract fun prixTTC(): Double

    fun retirerStock(quantite: Int): Boolean {
        if (quantite > 0 && quantite <= stock) {
            stock -= quantite
            return true
        } else {
            return false
        }
    }

    fun ajouterStock(quantite: Int) {
        stock += quantite
    }
}

class ProduitElectronique(
    code: String,
    nom: String,
    prixHt: Double,
    stock: Int,
    var garantieMois: Int
) : Produit(code, nom, prixHt, stock) {

    override fun prixTTC(): Double {
        return prixHt + prixHt * 20 / 100
    }

    fun appliquerRemise(pourcentage: Double) {
        prixHt -= prixHt * pourcentage / 100
    }
}

class ProduitAlimentaire(
    code: String,
    nom: String,
    prixHt: Double,
    stock: Int,
    var dateLimite: String
) : Produit(code, nom, prixHt, stock) {

    override fun prixTTC(): Double {
        return prixHt + prixHt * 10 / 100
    }
}

data class Client(
    val id: Int,
    val nom: String,
    val ville: String
)

class Commande(
    val numero: Int,
    val date: String,
    val client: Client,
    val produit: Produit,
    val quantite: Int
) {
    var statut: String = "CREATED"
        private set

    fun montantTotal(): Double {
        return produit.prixTTC() * quantite
    }

    fun payer(): Boolean {
        if (statut == "CREATED" && produit.retirerStock(quantite)) {
            statut = "PAID"
            return true
        } else {
            return false
        }
    }

    fun expedier(): Boolean {
        if (statut == "PAID") {
            statut = "SHIPPED"
            return true
        } else {
            return false
        }
    }

    fun annuler(): Boolean {
        if (statut == "SHIPPED" || statut == "CANCELLED") {
            return false
        }

        if (statut == "PAID") {
            produit.ajouterStock(quantite)
        }

        statut = "CANCELLED"
        return true
    }
}

fun main() {
    val c1 = Client(1, "Chihab", "Safi")
    val c2 = Client(2, "Ali", "Fes")
    val c3 = Client(3, "Aisam", "Tanger")

    val p1 = ProduitElectronique(
        "cc1", "PC Latitude 501", 1900.50, 20, 12
    )
    val p2 = ProduitElectronique(
        "cc2", "TV Sony 50", 5000.0, 60, 5
    )
    val p3 = ProduitAlimentaire(
        "cc3", "Banana", 5.0, 100, "2026-12-31"
    )

    val com1 = Commande(101, "2026-10-10", c1, p1, 2)
    val com2 = Commande(102, "2026-10-11", c1, p2, 1)
    val com3 = Commande(103, "2026-10-12", c2, p3, 5)
    val com4 = Commande(104, "2026-10-13", c2, p2, 200)
    val com5 = Commande(105, "2026-10-14", c3, p1, 1)
    val com6 = Commande(106, "2026-10-15", c3, p3, 10)

    val commandes = listOf(com1, com2, com3, com4, com5, com6)

    for (com in commandes) {
        println("Commande : ${com.numero}")
        println("Client : ${com.client.nom}")
        println("Produit : ${com.produit.nom}")
        println("Montant : ${com.montantTotal()}")
        println("Stock avant paiement : ${com.produit.stock}")

        println("Paiement : ${com.payer()}")
        println("Statut : ${com.statut}")
        println("Stock après paiement : ${com.produit.stock}")
       
    }

    println("Expédition com1 : ${com1.expedier()}")
    println("Statut com1 : ${com1.statut}")

    println("Annulation com1 : ${com1.annuler()}")

    println("Annulation com3 : ${com3.annuler()}")
    println("Stock banana : ${p3.stock}")

    var totalPayees = 0.0
    var totalAnnulees = 0.0

    for (com in commandes) {
        if (com.statut == "PAID" || com.statut == "SHIPPED") {
            totalPayees += com.montantTotal()
        }

        if (com.statut == "CANCELLED") {
            totalAnnulees += com.montantTotal()
        }
    }

    println("Total payé : $totalPayees")
    println("Total annulé : $totalAnnulees")
}