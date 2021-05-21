package tw.idv.jew.roomsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import tw.idv.jew.roomsample.databinding.ActivityMainBinding
import java.nio.file.Files.delete

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = UserAdapter(viewModel::update, viewModel::delete)
        viewModel.liveData.observe(this) {
            adapter.submitList(it)
        }
        binding.add.setOnClickListener {
            viewModel.addRandomUser()
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}