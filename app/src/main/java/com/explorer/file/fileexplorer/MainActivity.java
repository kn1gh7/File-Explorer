package com.explorer.file.fileexplorer;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<FileObj> allFilesInCurrDirectory;
    RecyclerView filenamesRV;
    ExplorerAdapter explorerAdapter;
    StringBuffer currentPath = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        allFilesInCurrDirectory = new ArrayList<FileObj>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        allFilesInCurrDirectory = getFilesFromRoot();

        filenamesRV = (RecyclerView) findViewById(R.id.filenamesRV);
        filenamesRV.setLayoutManager(new LinearLayoutManager(this));
        explorerAdapter = new ExplorerAdapter(this, allFilesInCurrDirectory);
        filenamesRV.setAdapter(explorerAdapter);
    }

    private List<FileObj> getFilesFromRoot() {
        return getFilesForPath("/");
    }

    private List<FileObj> getFilesForPath(String path) {
        Log.e("Getting Files at Path", path);
        List<FileObj> fileList = new ArrayList<FileObj>();

        File currentDirectory = new File(path);
        File[] files = currentDirectory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                FileObj fileObj = new FileObj();
                fileObj.setName(file.getName());
                fileObj.setType(FileObj.DIRECTORY);
                fileList.add(fileObj);//addAll(getListFiles(file));
            } else {
                    /*if(file.getName().endsWith(".csv")){
                        inFiles.add(file);
                    }*/
                FileObj fileObj = new FileObj();
                fileObj.setName(file.getName());
                fileObj.setType(FileObj.FILE);
                fileList.add(fileObj);
            }
        }
        return fileList;
    }

    public class FileObj {
        public static final int FILE = 1, DIRECTORY = 2;
        private String name;
        private int type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    private View.OnClickListener itemViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = filenamesRV.getChildAdapterPosition(v);
            if (allFilesInCurrDirectory.get(position).getType() == FileObj.DIRECTORY) {
                String newPath = "/" + allFilesInCurrDirectory.get(position).getName();
                updateViewWithPath(newPath);
            }
        }
    };

    private void updateViewWithPath(String path) {
        if (path == null || path.equals(""))
            path = "/";
        currentPath.append(path);
        List<FileObj> allFilesInDir = getFilesForPath(currentPath.toString());
        allFilesInCurrDirectory.clear();
        for (FileObj fileObj:allFilesInDir) {
            allFilesInCurrDirectory.add(fileObj);
            Log.e("FileNames", fileObj.getName());
        }

        explorerAdapter.notifyDataSetChanged();
        //filenamesRV.invalidate();
    }

    @Override
    public void onBackPressed() {
        if (currentPath != null && !currentPath.toString().equals("/")) {
            String newPath = currentPath.toString().substring(0, currentPath.toString().lastIndexOf("/"));
            Log.e("New Path", newPath);
            currentPath = new StringBuffer();
            updateViewWithPath(newPath);

            return;
        }
        super.onBackPressed();
    }

    private class ExplorerAdapter extends RecyclerView.Adapter<FilesViewHolder> {
        private Context context;
        private List<FileObj> allFilesInCurrentDirectory;

        public ExplorerAdapter(Context context, List<FileObj> files) {
            this.context = context;
            this.allFilesInCurrentDirectory = files;
        }

        @Override
        public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FilesViewHolder viewHolder;
            View view = getLayoutInflater().inflate(R.layout.item_view, null);
            view.setOnClickListener(itemViewClick);
            viewHolder = new FilesViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FilesViewHolder holder, int position) {
            if (allFilesInCurrentDirectory.get(position).getType() == FileObj.DIRECTORY) { //Directory
                holder.fileName.setText(allFilesInCurrentDirectory.get(position).getName() + " D");
            } else { //File
                holder.fileName.setText(allFilesInCurrentDirectory.get(position).getName() + " F");
            }
        }

        @Override
        public int getItemViewType(int position) {
            return allFilesInCurrentDirectory.get(position).getType();
        }

        @Override
        public int getItemCount() {
            if (allFilesInCurrentDirectory == null)
                return 0;

            return allFilesInCurrentDirectory.size();
        }
    }

    private class FilesViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;

        public FilesViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.filename);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
