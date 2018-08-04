//
//  ScanLineBackgroundView.swift
//  SeaTalk
//
//  Created by Wang Jinghan on 3/8/18.
//  Copyright © 2018 Garena. All rights reserved.
//

import UIKit

class ScanLineBackgroundView: UIView {
    var contentView: UIView? = nil
    var whiteFilter = UIView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        backgroundColor = .clear
        
        whiteFilter.backgroundColor = UIColor.init(white: 1, alpha: 0.2)
        addSubview(whiteFilter)
        whiteFilter.snp.remakeConstraints { (make) in
            make.edges.equalToSuperview()
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func updateContent(snapshotView: UIView?) {
        contentView?.removeFromSuperview()
        
        contentView = snapshotView
        
        if let view = contentView {
            insertSubview(view, belowSubview: whiteFilter)
            view.frame = CGRect(x: 3, y: 0, width: bounds.width, height: bounds.height)
        }
    }
}
