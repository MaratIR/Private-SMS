package com.privatemsg

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.privatemsg.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var store: ChatStore
    private lateinit var adapter: ChatListAdapter
    private var pendingOpenSetupAfterPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        store = ChatStore(this)

        adapter = ChatListAdapter(store.getChats()) { chat ->
            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHAT_ID, chat.id)
            })
        }
        binding.recyclerChats.layoutManager = LinearLayoutManager(this)
        binding.recyclerChats.adapter = adapter

        binding.fabNewChat.setOnClickListener {
            if (hasPermissions()) {
                startActivity(Intent(this, SetupActivity::class.java))
            } else {
                pendingOpenSetupAfterPermission = true
                showPermissionRationaleOrRequest()
            }
        }

        if (!hasPermissions()) {
            showPermissionRationaleOrRequest()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateChats(store.getChats())
    }

    private fun hasPermissions(): Boolean {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_CONTACTS
            )
        } else {
            arrayOf(
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_CONTACTS
            )
        }
        return perms.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun showPermissionRationaleOrRequest() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.permission_rationale_title)
                .setMessage(R.string.permission_rationale_message)
                .setPositiveButton(R.string.permission_allow) { _, _ -> doRequestPermissions() }
                .setNegativeButton(R.string.permission_deny) { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
                .show()
        } else {
            doRequestPermissions()
        }
    }

    private fun doRequestPermissions() {
        val perms = arrayOf(
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_CONTACTS
        )
        ActivityCompat.requestPermissions(this, perms, REQ_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQ_PERMISSIONS) return
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            if (pendingOpenSetupAfterPermission) {
                pendingOpenSetupAfterPermission = false
                startActivity(Intent(this, SetupActivity::class.java))
            }
        } else {
            pendingOpenSetupAfterPermission = false
            Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQ_PERMISSIONS = 100
    }
}
