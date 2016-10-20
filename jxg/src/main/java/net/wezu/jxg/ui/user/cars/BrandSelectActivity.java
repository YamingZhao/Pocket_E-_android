package net.wezu.jxg.ui.user.cars;

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
import net.wezu.widget.RoundImageview.RoundedNetImageView;
import net.wezu.widget.stickylistheaders.StickyListHeadersAdapter;
import net.wezu.widget.stickylistheaders.StickyListHeadersListView;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.CarBrand;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * @author snox@live.com
 * @date 2015/11/7.
 */
public class BrandSelectActivity extends BaseActivity {

    @Bind(android.R.id.list) StickyListHeadersListView stickyList;
    @Bind(R.id.refresh_layout)  SwipeRefreshLayout refreshLayout;
    @Bind(R.id.lsv_type) ListView listViewTypies;

    private BrandListAdapter mBrandsAdapter;

    private BaseListAdapter<CarBrand, AreaHolder> listTypies;

    private CarBrand brand1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_brand_select);

        setTitle("选择品牌");

        setDefaultBackButton();

        mBrandsAdapter = new BrandListAdapter(this);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBrands();
            }
        });

        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (FastClickUtil.isFastClick()) return;

                brand1 = (CarBrand) mBrandsAdapter.getItem(position);

                BrandSelectActivity.this.setResult(RESULT_OK, new Intent().putExtra("brand", brand1).putExtra("brand1", brand1));
                BrandSelectActivity.this.finish();

//                loadBrands(brand1.CardBrandId, new RequestManager.ResponseListener<List<CarBrand>>() {
//                    @Override
//                    public void success(List<CarBrand> result, String msg) {
//
//                        listTypies.clear();
//                        listTypies.addAll(result);
//
//                        listViewTypies.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void error(String msg) {
//
//                    }
//                });
            }
        });

        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);
        stickyList.setAdapter(mBrandsAdapter);

        listTypies = new BaseListAdapter<CarBrand, AreaHolder>(this, R.layout.listitem_brand) {
            @Override
            protected AreaHolder buildViewHolder(Context context, View convertView) {
                return new AreaHolder(BrandSelectActivity.this, convertView);
            }
        };


        listViewTypies.setAdapter(listTypies);
        listViewTypies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CarBrand brand = (CarBrand) listTypies.getItem(position);

                BrandSelectActivity.this.setResult(RESULT_OK, new Intent().putExtra("brand", brand).putExtra("brand1", brand1));
                BrandSelectActivity.this.finish();
            }
        });

        //CarBrand brand = getIntent().getParcelableExtra("brand");

        loadBrands();
    }

    private void loadBrands() {

        refreshLayout.setRefreshing(true);

        loadBrands(0, new RequestManager.ResponseListener<List<CarBrand>>() {
            @Override
            public void success(List<CarBrand> result, String msg) {
                mBrandsAdapter.clear();
                mBrandsAdapter.addAll(result);

                refreshLayout.setRefreshing(false);
            }

            @Override
            public void error(String msg) {

            }
        });
    }


    private void loadBrands(int parentId, RequestManager.ResponseListener<List<CarBrand>> listener) {

        Map<String, String> params = null;
        if (parentId > 0) {
            params = new HashMap<>();
            params.put("parentid", String.valueOf(parentId));
        }

        RequestManager.getInstance().getList("listbrands", requestTag, params, CarBrand.class, listener);
    }

    public class BrandListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

        private Context mContext;

        private int[] mSectionIndices;
        private List<String> mHeaders;
        private List<CarBrand> mCarBrands;

        private LayoutInflater mInflater;

        public BrandListAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);

            mHeaders = new ArrayList<>();
            mCarBrands = new ArrayList<>();
        }

        public void addAll(List<CarBrand> brands) {
            ArrayList<Integer> sections = new ArrayList<>();
            int pos = 0;
            for(CarBrand brand : brands) {
                mCarBrands.add(brand);
                if (!mHeaders.contains(brand.FirstLetter)) {
                    mHeaders.add(brand.FirstLetter);
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
            mCarBrands.clear();
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

                holder.text.setText(mCarBrands.get(position).FirstLetter);

                return convertView;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getHeaderId(int position) {
            return mCarBrands.get(position).FirstLetter.charAt(0);
        }

        @Override
        public int getCount() {
            return mCarBrands.size();
        }

        @Override
        public Object getItem(int position) {
            return mCarBrands.get(position);
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
                    convertView = mInflater.inflate(R.layout.listitem_brand, parent, false);
                    holder.image = (RoundedNetImageView) convertView.findViewById(R.id.iv_brand_logo);
                    holder.text = (TextView) convertView.findViewById(R.id.txt_brand_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.text.setText(mCarBrands.get(position).BrandName);
                holder.image.setImageUrl(mCarBrands.get(position).BrandLogo, Application.getInstance().getImageLoader());

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
            RoundedNetImageView image;
        }
    }

    public class AreaHolder extends BaseViewHolder<CarBrand> {

        private TextView txtPlateNo;
        RoundedNetImageView image;

        public AreaHolder(Context context, View view) {
            super(context, view);

            txtPlateNo = (TextView) view.findViewById(R.id.txt_brand_name);
            image = (RoundedNetImageView) view.findViewById(R.id.iv_brand_logo);
        }

        @Override
        public void setData(CarBrand data) {
            txtPlateNo.setText(data.BrandName);
            image.setImageUrl(data.BrandLogo, Application.getInstance().getImageLoader());
        }
    }
}
