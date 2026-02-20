package cz.cvut.fit.bp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform