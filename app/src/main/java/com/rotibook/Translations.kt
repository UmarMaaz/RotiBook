package com.rotibook

object Translations {
    enum class Language {
        ENGLISH, URDU
    }

    private val translations = mapOf(
        "app_name" to mapOf(
            Language.ENGLISH to "RotiTracker",
            Language.URDU to "روٹی ٹریکر"
        ),
        "search_clients" to mapOf(
            Language.ENGLISH to "Search clients",
            Language.URDU to "گاہکوں کو تلاش کریں"
        ),
        "add_client" to mapOf(
            Language.ENGLISH to "Add Client",
            Language.URDU to "گاہک شامل کریں"
        ),
        "client_name" to mapOf(
            Language.ENGLISH to "Client Name",
            Language.URDU to "گاہک کا نام"
        ),
        "add" to mapOf(
            Language.ENGLISH to "Add",
            Language.URDU to "شامل کریں"
        ),
        "cancel" to mapOf(
            Language.ENGLISH to "Cancel",
            Language.URDU to "منسوخ کریں"
        ),
        "quantity" to mapOf(
            Language.ENGLISH to "Quantity",
            Language.URDU to "مقدار"
        ),
        "date" to mapOf(
            Language.ENGLISH to "Date",
            Language.URDU to "تاریخ"
        ),
        "edit" to mapOf(
            Language.ENGLISH to "Edit",
            Language.URDU to "ترمیم کریں"
        ),
        "delete" to mapOf(
            Language.ENGLISH to "Delete",
            Language.URDU to "حذف کریں"
        ),
        "add_purchase" to mapOf(
            Language.ENGLISH to "Add Purchase",
            Language.URDU to "خریداری شامل کریں"
        ),
        "language" to mapOf(
            Language.ENGLISH to "Language",
            Language.URDU to "زبان"
        ),
        "calculator" to mapOf(
            Language.ENGLISH to "Calculator",
            Language.URDU to "کیلکولیٹر"
        ),
        "roti_count" to mapOf(
            Language.ENGLISH to "Roti Count",
            Language.URDU to "روٹی کی تعداد"
        ),
        "price_per_roti" to mapOf(
            Language.ENGLISH to "Price per Roti",
            Language.URDU to "فی روٹی قیمت"
        ),
        "total_price" to mapOf(
            Language.ENGLISH to "Total Price",
            Language.URDU to "کل قیمت"
        ),
        "close" to mapOf(
            Language.ENGLISH to "Close",
            Language.URDU to "بند کریں"
        ),
        "client_details" to mapOf(
            Language.ENGLISH to "Client Details",
            Language.URDU to "گاہک کی تفصیلات"
        ),
        "phone" to mapOf(
            Language.ENGLISH to "Phone",
            Language.URDU to "فون"
        ),
        "back" to mapOf(
            Language.ENGLISH to "Back",
            Language.URDU to "واپس"
        ),
        "edit_client" to mapOf(
            Language.ENGLISH to "Edit Client",
            Language.URDU to "گاہک میں ترمیم کریں"
        ),
        "delete_client" to mapOf(
            Language.ENGLISH to "Delete Client",
            Language.URDU to "گاہک کو حذف کریں"
        ),
        "delete_client_confirmation" to mapOf(
            Language.ENGLISH to "Are you sure you want to delete client %s?",
            Language.URDU to "کیا آپ واقعی گاہک %s کو حذف کرنا چاہتے ہیں؟"
        ),
        "update" to mapOf(
            Language.ENGLISH to "Update",
            Language.URDU to "اپ ڈیٹ کریں"
        ),
        "data_export_import" to mapOf(
            Language.ENGLISH to "Data Export/Import",
            Language.URDU to "ڈیٹا برآمد/درآمد"
        ),
        "export_data" to mapOf(
            Language.ENGLISH to "Export Data",
            Language.URDU to "ڈیٹا برآمد کریں"
        ),
        "import_data" to mapOf(
            Language.ENGLISH to "Import Data",
            Language.URDU to "ڈیٹا درآمد کریں"
        ),
        "export_success" to mapOf(
            Language.ENGLISH to "Data exported successfully",
            Language.URDU to "ڈیٹا کامیابی سے برآمد کیا گیا"
        ),
        "export_error" to mapOf(
            Language.ENGLISH to "Error exporting data",
            Language.URDU to "ڈیٹا برآمد کرنے میں خرابی"
        ),
        "import_success" to mapOf(
            Language.ENGLISH to "Data imported successfully",
            Language.URDU to "ڈیٹا کامیابی سے درآمد کیا گیا"
        ),
        "import_error" to mapOf(
            Language.ENGLISH to "Error importing data",
            Language.URDU to "ڈیٹا درآمد کرنے میں خرابی"
        ),
        "data_management_description" to mapOf(
            Language.ENGLISH to "Manage your RotiTracker data by exporting or importing. This helps you backup your data or transfer it to a new device.",
            Language.URDU to "روٹی ٹریکر ڈیٹا کو برآمد یا درآمد کر کے منظم کریں۔ یہ آپ کے ڈیٹا کا بیک اپ لینے یا اسے نئے آلے پر منتقل کرنے میں مدد کرتا ہے۔"
        ),
        "export_data_description" to mapOf(
            Language.ENGLISH to "Save your current data to a file. You can use this file to restore your data later or on another device.",
            Language.URDU to "اپنا موجودہ ڈیٹا فائل میں محفوظ کریں۔ آپ اس فائل کو بعد میں یا کسی دوسرے آلے پر اپنا ڈیٹا بحال کرنے کے لیے استعمال کر سکتے ہیں۔"
        ),
        "import_data_description" to mapOf(
            Language.ENGLISH to "Load data from a previously exported file. This will replace your current data.",
            Language.URDU to "پہلے برآمد کی گئی فائل سے ڈیٹا لوڈ کریں۔ یہ آپ کے موجودہ ڈیٹا کو تبدیل کر دے گا۔"
        ),
        "export" to mapOf(
            Language.ENGLISH to "Export",
            Language.URDU to "برآمد کریں"
        ),
        "import" to mapOf(
            Language.ENGLISH to "Import",
            Language.URDU to "درآمد کریں"
        ),
        "operation_success" to mapOf(
            Language.ENGLISH to "Operation completed successfully",
            Language.URDU to "آپریشن کامیابی سے مکمل ہو گیا"
        ),
        "operation_error" to mapOf(
            Language.ENGLISH to "An error occurred",
            Language.URDU to "ایک خرابی پیش آگئی"
        ),
        "clear_search" to mapOf(
            Language.ENGLISH to "Clear search",
            Language.URDU to "تلاش صاف کریں"
        )
    )

    fun get(key: String, language: Language): String {
        return translations[key]?.get(language) ?: key
    }
}

