//
//  FindPeopleViewController.swift
//  SeaTalk
//
//  Created by Wang Jinghan on 4/8/18.
//  Copyright © 2018 Garena. All rights reserved.
//

import UIKit
import SeaTalkCore

class FindPeopleViewController: ViewController {

    let mapView = ScanLineMapView()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        navigationController?.navigationBar.barStyle = .blackOpaque
        navigationController?.navigationBar.tintColor = .white
        navigationController?.navigationBar.setBackgroundImage(UIColor.mfrgNavBarColor.colorImage()!, for: .default)
        
        let item = UIBarButtonItem(barButtonSystemItem: .search, target: self, action: #selector(didPressSearch))
        self.navigationItem.rightBarButtonItem = item
        
        view.addSubview(mapView)
        
        mapView.snp.remakeConstraints { (make) in
            make.edges.equalToSuperview()
        }
    }
    
    @objc func didPressSearch() {
        let provider = UserSelectBuddyProvider()
        let opts = UserSelectViewController.Options(minSelectCount: nil, maxSelectCount: 1)
        
        let vc = UserSelectViewController(provider: provider, options: opts)
        vc.didReachLimit = { [weak vc] in
            vc?.hudShowErrorText("You can only choose one at a time")
        }
        vc.didSelectUsers = { [weak self, weak vc] (userIDArr) in
            guard let me = self, let vc = vc else { return }
            guard let selected = userIDArr.first else { return }
            
            if selected == 10067 {
                me.hudShowLoading()
            } else {
                me.hudShowSuccessText("User is not found in office at this moment.")
            }
            vc.dismiss(animated: true, completion: nil)
        }
        
        let nvc = vc.embedInNavigationController()
        
        self.present(nvc, animated: true, completion: nil)
    }
}
