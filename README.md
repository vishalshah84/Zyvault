# Zyvault - The Financial Super App

Zyvault is a comprehensive financial management application designed to help users track their spending, manage insurance, verify identity, and connect with their bank accounts securely.

## 🚀 Features

- **Finance Dashboard**: Connect your bank accounts using Plaid to view real-time balances and transaction history.
- **Transfer Money**: Seamlessly move funds between linked accounts using the Plaid Transfer API.
- **Insurance Vault**: Manage and track your House, Health, and Vehicle insurance policies in one place.
- **Identity Verification**: Secure onboarding with Stripe Identity SDK for ID and face scanning.
- **Bill Tracking**: Upload and monitor your monthly bills with automated OCR (Coming soon).
- **Clean Slate UI**: Data-driven components that stay in sync with your live financial records.

## 🛠️ Tech Stack

- **UI**: Jetpack Compose (100% Kotlin)
- **Backend**: Firebase (Firestore, Authentication)
- **Networking**: Retrofit, OkHttp
- **Financial APIs**: Plaid Link & Transfer SDKs
- **Verification**: Stripe Identity SDK
- **Architecture**: MVVM (Model-View-ViewModel)

## 📦 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17
- Firebase account with Firestore and Auth enabled

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/zyvault.git
   ```
2. Open the project in Android Studio.
3. Add your `google-services.json` to the `app/` directory.
4. Sync Gradle and run the app.

## 🔒 Security
Zyvault does not store your bank credentials. All connectivity is handled via secure, encrypted tokens provided by Plaid and Stripe.

## 📝 License
Proprietary. All rights reserved.
