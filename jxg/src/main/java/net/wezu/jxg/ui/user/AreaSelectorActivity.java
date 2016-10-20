package net.wezu.jxg.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.stickylistheaders.StickyListHeadersAdapter;
import net.wezu.widget.stickylistheaders.StickyListHeadersListView;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Area;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class AreaSelectorActivity extends BaseActivity {

    ListView lvCarList;
    private BaseListAdapter<Area, AreaHolder> mAreaes;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;

    private AreaListAdapter mRegionAdapter;

    private String mRegion;
    private String mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_area_select);

        setTitle("选择地址");

        setDefaultBackButton();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRegions();
            }
        });

        mRegionAdapter = new AreaListAdapter(this);

        stickyList = (StickyListHeadersListView) findViewById(R.id.list_region);
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (FastClickUtil.isFastClick()) return;

                Area area = (Area)mRegionAdapter.getItem(position);

                mRegion = area.Text;

                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("region", area.Text);

                RequestManager.getInstance().getList("listcities", requestTag, parameters, Area.class, new RequestManager.ResponseListener<List<Area>>() {
                    @Override
                    public void success(List<Area> result, String msg) {
                        mAreaes.clear();
                        mAreaes.addAll(result);

                        lvCarList.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void error(String msg) {

                    }
                });
            }
        });

        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);
        stickyList.setAdapter(mRegionAdapter);

        mAreaes = new BaseListAdapter<Area, AreaHolder>(this, R.layout.listitem_area) {

            @Override
            protected AreaHolder buildViewHolder(Context context, View convertView) {
                return new AreaHolder(AreaSelectorActivity.this, convertView);
            }
        };

        lvCarList = (ListView) findViewById(android.R.id.list);
        lvCarList.setAdapter(mAreaes);
        lvCarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (FastClickUtil.isFastClick()) return;

                Area area = (Area)mAreaes.getItem(position);

                AreaSelectorActivity.this.setResult(RESULT_OK, new Intent().putExtra("region", mRegion).putExtra("city", area.Text));
                AreaSelectorActivity.this.finish();
            }
        });

        loadRegions();
    }

    private void loadRegions() {
        RequestManager.getInstance().getList("listregions", requestTag, null, Area.class, new RequestManager.ResponseListener<List<Area>>() {
            @Override
            public void success(List<Area> result, String msg) {
                //mAreaes.clear();
                //mAreaes.addAll(result);
                mRegionAdapter.clear();
                mRegionAdapter.addAll(result);

                refreshLayout.setRefreshing(false);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    public class AreaHolder extends BaseViewHolder<Area> {

        private TextView txtPlateNo;

        public AreaHolder(Context context, View view) {
            super(context, view);

            txtPlateNo = (TextView) view.findViewById(R.id.txt_name);
        }

        @Override
        public void setData(Area data) {
            txtPlateNo.setText(data.Text);
        }
    }

    public class AreaListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

        private Context mContext;

        private int[] mSectionIndices;
        private List<String> mHeaders;
        private List<Area> mAreaes;

        private LayoutInflater mInflater;

        public AreaListAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);

            mHeaders = new ArrayList<>();
            mAreaes = new ArrayList<>();
        }

        public void addAll(List<Area> areaes) {
            ArrayList<Integer> sections = new ArrayList<>();
            int pos = 0;
            for(Area area : areaes) {
                mAreaes.add(area);
                if (!mHeaders.contains(area.FirstLetter)) {
                    mHeaders.add(area.FirstLetter);
                    sections.add(pos);
                }
                pos++;
            }

            mSectionIndices = new int[sections.size()];
            for (int i = 0; i < sections.size(); i++) {
                mSectionIndices[i] = sections.get(i);
            }


            notifyDataSetChanged();
        }

        public void clear() {
            mHeaders.clear();
            mAreaes.clear();
            notifyDataSetInvalidated();
        }

        @Override
        public int getSectionForPosition(int position) {
            for (int i = 0; i < mSectionIndices.length; i++) {
                if (position < mSectionIndices[i]) {
                    return i - 1;
                }
            }
            return mSectionIndices.length - 1;
        }

        @Override
        public Object[] getSections() {
            return mHeaders.toArray();
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            if (mSectionIndices.length == 0) {
                return 0;
            }

            if (sectionIndex >= mSectionIndices.length) {
                sectionIndex = mSectionIndices.length - 1;
            } else if (sectionIndex < 0) {
                sectionIndex = 0;
            }
            return mSectionIndices[sectionIndex];        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            try {
                HeaderViewHolder holder;

                if (convertView == null) {
                    holder = new HeaderViewHolder();
                    convertView = mInflater.inflate(R.layout.listitem_common_header, parent, false);
                    holder.text = (TextView) convertView.findViewById(R.id.txt_name);
                    convertView.setTag(holder);
                } else {
                    holder = (HeaderViewHolder) convertView.getTag();
                }

                holder.text.setText(mAreaes.get(position).FirstLetter);

                return convertView;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getHeaderId(int position) {
            return mAreaes.get(position).FirstLetter.charAt(0);
        }

        @Override
        public int getCount() {
            return mAreaes.size();
        }

        @Override
        public Object getItem(int position) {
            return mAreaes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            try {
                ViewHolder holder;

                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.listitem_area, parent, false);
                    holder.text = (TextView) convertView.findViewById(R.id.txt_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.text.setText(mAreaes.get(position).Text);

                return convertView;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        class HeaderViewHolder {
            TextView text;
        }

        class ViewHolder {
            TextView text;
        }
    }
}
