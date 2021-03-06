package tw.idv.jew.roomsample

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class MigrationTest {
    private val TEST_DB = "test_db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        UserDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO `users` VALUES ('Peter', '23', '1')")
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 2, true, UserDatabase.MIGRATION_1_2).apply {
            val c = query("SELECT * FROM users WHERE user = 'Peter'")
            assert(c.count == 1)
            if (c.moveToNext()) {
                assert(c.getInt(1) == 23)
            } else {
                assert(false)
            }
            close()
        }
    }

    @Test
    fun migrateAll() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO `users` VALUES ('Peter', '23', '1')")
            close()
        }
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            UserDatabase::class.java,
            TEST_DB
        ).addMigrations(UserDatabase.MIGRATION_1_2).build().apply {
            checkUser(this.userDao(), "Peter", 23)
            close()
        }
    }

    private fun checkUser(userDao: UserDao, name: String, age: Int) = runBlocking {
        val user = userDao.getUser(name)
        assert(user.age == age)
    }
}