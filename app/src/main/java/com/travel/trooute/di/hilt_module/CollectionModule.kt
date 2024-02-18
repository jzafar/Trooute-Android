package com.travel.trooute.di.hilt_module

import com.travel.trooute.core.util.Constants.INBOX_COLLECTION_NAME
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InboxCollection

@Module
@InstallIn(SingletonComponent::class)
object CollectionModule {

    @Singleton
    @InboxCollection
    @Provides
    fun provideInboxCollectionRef(fireStore: FirebaseFirestore): CollectionReference {
        return fireStore.collection(INBOX_COLLECTION_NAME)
    }
}