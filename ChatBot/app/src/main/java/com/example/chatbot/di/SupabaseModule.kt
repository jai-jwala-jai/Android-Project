package com.example.chatbot.di

import com.example.chatbot.BuildConfig
import com.example.chatbot.repository.AuthRepository
import com.example.chatbot.repository.AuthRepositoryImpl
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.repository.ChatRepositoryImpl
import com.example.chatbot.repository.MessageRepository
import com.example.chatbot.repository.MessageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import jakarta.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SupabaseModule {
    private val url = BuildConfig.SUPABASE_URL
    private val key = BuildConfig.SUPABASE_KEY

    @OptIn(SupabaseInternal::class)
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            httpEngine = OkHttp.create()

            httpConfig {
                install(WebSockets)
            }

            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseDatabase(client: SupabaseClient): Postgrest =
        client.postgrest

    @Provides
    @Singleton
    fun provideSupabaseAuth(client: SupabaseClient): Auth =
        client.auth

    @Provides
    @Singleton
    fun provideAuthRepository(auth: Auth): AuthRepository =
        AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideChatRepository(postgrest: Postgrest): ChatRepository =
        ChatRepositoryImpl(postgrest)

    @Provides
    @Singleton
    fun provideMessageRepository(postgrest: Postgrest): MessageRepository =
        MessageRepositoryImpl(postgrest)
}