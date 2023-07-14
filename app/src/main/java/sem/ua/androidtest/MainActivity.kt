package sem.ua.androidtest

import WebViewTestFragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import sem.ua.androidtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gameBtn.setOnClickListener {
            binding.webBtn.visibility = View.VISIBLE
            binding.gameBtn.visibility = View.VISIBLE
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.webBtn.setOnClickListener {
            binding.webBtn.visibility = View.GONE
            binding.gameBtn.visibility = View.GONE
            val fragment = WebViewTestFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}
