package app.lawnchair.lawnicons.ui.destination.about

data class Contributor(
    val name: String,
    val username: String? = null,
    val photoUrl: String,
    val socialUrl: String? = null,
    val descriptionRes: Int? = null,
)
