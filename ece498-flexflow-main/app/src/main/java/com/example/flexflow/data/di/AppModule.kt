package com.example.flexflow.data.di

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Room
import com.example.flexflow.data.room.EventDao
import com.example.flexflow.data.room.FlexFlowDatabase
import com.example.flexflow.data.room.JournalDao
import com.example.flexflow.data.room.ScheduleDao
import com.example.flexflow.data.room.TaskDao
import com.example.flexflow.data.room.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext applicationContext: Context): FlexFlowDatabase {
        System.loadLibrary("sqlcipher")

        val password = "ayy"
        val factory: SupportOpenHelperFactory = SupportOpenHelperFactory(password.toByteArray())

        try {
            val database = SQLiteDatabase.openOrCreateDatabase(
                File(applicationContext.getDatabasePath("flexflow-db").absolutePath),
                "",
                null,
                null
            )
            database.close()
            encrypt(applicationContext, File(applicationContext.getDatabasePath("flexflow-db").absolutePath), password.toByteArray())
        } catch (ex: SQLiteException) {
            println("launched with encrypted roomDB")
        }

        return Room.databaseBuilder(
            applicationContext,
            FlexFlowDatabase::class.java,
            "flexflow-db"
        )
            .fallbackToDestructiveMigration() // TODO - added this temp to allow building, https://developer.android.com/training/data-storage/room/migrating-db-versions
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    @Singleton
    fun provideJournalDao(db: FlexFlowDatabase): JournalDao {
        return db.journalDao()
    }

    @Provides
    @Singleton
    fun provideScheduleDao(db: FlexFlowDatabase): ScheduleDao {
        return db.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(db: FlexFlowDatabase): TaskDao {
        return db.taskDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: FlexFlowDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(db: FlexFlowDatabase): EventDao {
        return db.eventDao()
    }
}

// https://stackoverflow.com/a/73257066
private fun encrypt(context: Context, originalFile: File, passphrase: ByteArray) {
    System.loadLibrary("sqlcipher")
    if (originalFile.exists()) {
        val newFile = File(context.cacheDir, "sqlcipherutils.db")
        if (!newFile.createNewFile()) {
            throw FileNotFoundException(newFile.absolutePath.toString() + " not created")
        }

        // get database version from existing database
        val databaseVersion = SQLiteDatabase.openOrCreateDatabase(originalFile.absolutePath, "", null, null).use { database ->
            database.version
        }

        SQLiteDatabase.openDatabase(
            newFile.absolutePath, passphrase, null,
            SQLiteDatabase.OPEN_READWRITE, null, null
        ).use { temporaryDatabase ->
            temporaryDatabase.rawExecSQL("ATTACH DATABASE '${originalFile.absolutePath}' AS sqlcipher4 KEY ''")
            temporaryDatabase.rawExecSQL("SELECT sqlcipher_export('main', 'sqlcipher4')")
            temporaryDatabase.rawExecSQL("DETACH DATABASE sqlcipher4")
            temporaryDatabase.version = databaseVersion
        }
        originalFile.delete()
        newFile.renameTo(originalFile)
    } else {
        throw FileNotFoundException(originalFile.absolutePath.toString() + " not found")
    }
}
